package concurrency;

public class SynchronizedCounter {

  public static void main(String[] args) throws InterruptedException {
    InventoryCounter counter = new InventoryCounter(0);
    IncrementingThread incrementing1 = new IncrementingThread(counter, 1000);
    DecrementingThread decrementing1 = new DecrementingThread(counter, 1000);

    incrementing1.start();
    decrementing1.start();
    incrementing1.join();
    decrementing1.join();

    System.out.println("Items count: " + counter.getItems());
  }

  private static class InventoryCounter {
    private int items;

    public InventoryCounter(int items) {
      this.items = items;
    }

    synchronized public void increment() {
      items++;
    }

    synchronized public void decrement() {
      items--;
    }

    public int getItems() {
      return items;
    }
  }

  public static class IncrementingThread extends Thread {
    private InventoryCounter counter;
    private int incrementsLeft;

    IncrementingThread(InventoryCounter counter, int incrementsLeft) {
      this.counter = counter;
      this.incrementsLeft = incrementsLeft;
    }

    @Override
    public void run() {
      while (incrementsLeft > 0) {
        this.counter.increment();
        this.incrementsLeft--;
      }
    }
  }

  public static class DecrementingThread extends Thread {
    private InventoryCounter counter;
    private int decrementsLeft;

    public DecrementingThread(InventoryCounter counter, int decrementsLeft) {
      this.counter = counter;
      this.decrementsLeft = decrementsLeft;
    }

    @Override
    public void run() {
      while (decrementsLeft > 0) {
        this.counter.decrement();
        this.decrementsLeft--;
      }
    }
  }
}
