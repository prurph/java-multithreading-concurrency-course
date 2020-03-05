package thread.creation;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultiExecutor {
  private List<Runnable> tasks;

  public MultiExecutor(List<Runnable> tasks) {
    this.tasks = tasks;
  }

  public static void main(String[] args) {
    List<Runnable> tasks =
        Stream.of(0, 1, 2, 3).map((i) -> (Runnable) () -> System.out.println(i)).collect(Collectors.toList());
    MultiExecutor me = new MultiExecutor(tasks);
    me.executeAll();
  }

  public void executeAll() {
    this.tasks.forEach((runnable) -> {
      Thread thread = new Thread(runnable);
      thread.start();
    });
  }
}


