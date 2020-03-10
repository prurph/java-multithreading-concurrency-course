package concurrency.deadlock;

import java.util.Random;

public class Main {
  public static void main(String[] args) {
    Intersection intersection = new Intersection();
    Thread trainA = new Thread(new TrainA(intersection));
    Thread trainB = new Thread(new TrainB(intersection));

    trainA.start();
    trainB.start();
  }

  public static class TrainA implements Runnable {
    private Intersection intersection;
    private Random random = new Random();

    public TrainA(Intersection intersection) {
      this.intersection = intersection;
    }

    @Override
    public void run() {
      while (true) {
        try {
          Thread.sleep(random.nextInt(5));
        } catch (InterruptedException ignored) {
        }
        intersection.takeRoadA();
      }
    }
  }

  public static class TrainB implements Runnable {
    private Intersection intersection;
    private Random random = new Random();

    public TrainB(Intersection intersection) {
      this.intersection = intersection;
    }

    @Override
    public void run() {
      while (true) {
        try {
          Thread.sleep(random.nextInt(5));
        } catch (InterruptedException ignored) {
        }
        intersection.takeRoadB();
      }
    }
  }

  public static class Intersection {
    private final Object roadA = new Object();
    private final Object roadB = new Object();

    public void takeRoadA() {
      // Same order of lock acquisition: avoid circular wait and hence avoid deadlock
      synchronized (roadA) {
        System.out.println("🛣 Road A locked by thread: " + Thread.currentThread().getName());
        synchronized (roadB) {
          System.out.println("🚂 Train passing through Road A.");
          try {
            Thread.sleep(1);
          } catch (InterruptedException ignore) {
          }
        }
      }
    }

    public void takeRoadB() {
      // Same order of lock acquisition: avoid circular wait and hence avoid deadlock
      synchronized (roadA) {
        System.out.println("🛣 Road A locked by thread: " + Thread.currentThread().getName());
        synchronized (roadB) {
          System.out.println("🚂 Train passing through Road B.");
          try {
            Thread.sleep(1);
          } catch (InterruptedException ignore) {
          }
        }
      }
    }
  }
}
