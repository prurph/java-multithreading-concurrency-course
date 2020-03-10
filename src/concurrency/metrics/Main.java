package concurrency.metrics;

import java.util.List;
import java.util.Random;

public class Main {

  public static void main(String[] args) {
    Metrics metrics = new Metrics();
    List<BusinessLogic> blThreads = List.of(new BusinessLogic(metrics), new BusinessLogic(metrics));
    MetricsPrinter metricsPrinter = new MetricsPrinter(metrics);

    blThreads.forEach(Thread::start);
    metricsPrinter.start();
  }

  public static class MetricsPrinter extends Thread {
    private Metrics metrics;

    public MetricsPrinter(Metrics metrics) {
      this.metrics = metrics;
    }

    @Override
    public void run() {
      while (true) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
        System.out.println("Current average: " + metrics.getAverage());
      }
    }
  }

  public static class BusinessLogic extends Thread {
    private Metrics metrics;
    private Random random = new Random();

    public BusinessLogic(Metrics metrics) {
      this.metrics = metrics;
    }

    @Override
    public void run() {
      while (true) {
        long start = System.currentTimeMillis();
        try {
          Thread.sleep(random.nextInt(10));
        } catch (InterruptedException ignored) {
        }
        long end = System.currentTimeMillis();

        metrics.addSample(end - start);
      }
    }
  }

  private static class Metrics {
    private long count = 0;
    private volatile double average = 0.0;

    public synchronized void addSample(long sample) {
      double currentSum = average * count;
      count++;
      average = (currentSum + sample) / count;
    }

    public double getAverage() {
      return average;
    }
  }
}
