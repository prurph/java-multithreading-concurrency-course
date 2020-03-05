package thread.creation.example2;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Main {
  public static final int MAX_PASSWORD = 9999;

  public static void main(String[] args) {
    Vault vault = new Vault(ThreadLocalRandom.current().nextInt(MAX_PASSWORD + 1));
    List<Thread> threads = Arrays.asList(new AscendingHackerThread(vault),
        new DescendingHackerThread(vault), new RandomHackerThread(vault), new PoliceThread());
    threads.forEach(Thread::start);
  }

  private static class Vault {
    private int password;

    public Vault(int password) {
      this.password = password;
    }

    public boolean isCorrectPassword(int guess) {
      try {
        Thread.sleep(2);
      } catch (InterruptedException ignored) {
      }
      return this.password == guess;
    }
  }

  private static abstract class HackerThread extends Thread {
    protected Vault vault;

    public HackerThread(Vault vault) {
      this.vault = vault;
      this.setName(this.getClass().getSimpleName());
      this.setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public synchronized void start() {
      System.out.println("Starting thread: " + this.getName());
      super.start();
    }

    void guessPassword(int guess) {
      if (vault.isCorrectPassword(guess)) {
        System.out.println("👨‍💻 " + this.getName() + " hacked the password: " + guess);
        System.exit(0);
      }
    }
  }

  private static class AscendingHackerThread extends HackerThread {
    public AscendingHackerThread(Vault vault) {
      super(vault);
    }

    @Override
    public void run() {
      IntStream.rangeClosed(0, MAX_PASSWORD).forEach(this::guessPassword);
    }
  }

  private static class DescendingHackerThread extends HackerThread {
    public DescendingHackerThread(Vault vault) {
      super(vault);
    }

    @Override
    public void run() {
      IntStream.rangeClosed(MAX_PASSWORD, 0).forEach(this::guessPassword);
    }
  }

  private static class RandomHackerThread extends HackerThread {
    public RandomHackerThread(Vault vault) {
      super(vault);
    }

    @Override
    public void run() {
      while (true) {
        guessPassword(ThreadLocalRandom.current().nextInt(MAX_PASSWORD + 1));
      }
    }
  }

  private static class PoliceThread extends Thread {
    @Override
    public void run() {
      IntStream.iterate(10, i -> i > 0, i -> i - 1).forEach((remainingSecs) -> {
        System.out.println("👮 ‍️Police thread chasing hackers! Time remaining: " + remainingSecs);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
      });
      System.out.println("🚨 Hackers busted!");
      System.exit(0);
    }
  }
}
