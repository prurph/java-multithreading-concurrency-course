package throughput;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThroughputHttpServer {
  private static final String INPUT_FILE = "resources/throughput/war_and_peace.txt";
  private static final int NUMBER_OF_THREADS = 8;

  public static void main(String[] args) throws IOException {
    String text = new String(Files.readAllBytes(Paths.get(INPUT_FILE)));
    startServer(text);
  }

  public static void startServer(String text) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
    server.createContext("/search", new WordCountHandler(text));
    Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    server.setExecutor(executor);
    server.start();
  }

  private static class WordCountHandler implements HttpHandler {
    private String text;

    public WordCountHandler(String text) {
      this.text = text;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
      var query = httpExchange.getRequestURI().getQuery();
      var keyValue = query.split("=");
      var action = keyValue[0];
      var word = keyValue[1];
      if (!action.equals("word")) {
        httpExchange.sendResponseHeaders(400, 0);
        return;
      }

      var count = countWord(word);
      var response = Long.toString(count).getBytes();
      httpExchange.sendResponseHeaders(200, response.length);
      var outputStream = httpExchange.getResponseBody();
      outputStream.write(response);
      outputStream.close();
    }

    private long countWord(String word)  {
      long count = 0;
      int index = 0;
      while (index >= 0) {
        index = text.indexOf(word, index);
        if (index >= 0) {
          count++;
          index++;
        }
      }
      return count;
    }
  }
}
