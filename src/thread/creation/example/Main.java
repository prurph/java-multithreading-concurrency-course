package thread.creation.example;

public class Main {
  public static void main(String[] args) {
    Thread thread =
        new Thread(() -> {
          System.out.println("In thread.run() for: " + Thread.currentThread().getName());
          System.out.println("Thread priority is: " + Thread.currentThread().getPriority());
          throw new RuntimeException("💥 INTENTIONAL EXCEPTION 💥");
        });

    thread.setName("Misbehaving thread");
    thread.setPriority(Thread.MAX_PRIORITY);
    thread.setUncaughtExceptionHandler((t, e) -> {
      System.out.println("Critical error in thread " + t.getName() + ": " + e.getMessage());
    });

    System.out.println("In thread: " + Thread.currentThread().getName() + " before start()");
    thread.start();
    System.out.println("In thread: " + Thread.currentThread().getName() + " after start()");
  }
}
