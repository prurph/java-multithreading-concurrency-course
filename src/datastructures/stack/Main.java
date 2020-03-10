package datastructures.stack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class Main {

  public static void main(String[] args) throws InterruptedException {
    int INITIAL_STACK_SIZE = 10000;
    int PUSHING_THREAD_COUNT = 2;
    int POPPING_THREAD_COUNT = 2;

    // LockStack<Integer> stack = new LockStack<>();
    LockFreeStack<Integer> stack = new LockFreeStack<>();
    List<Thread> threads = new ArrayList<>();
    ThreadLocalRandom.current().ints(INITIAL_STACK_SIZE).forEach(stack::push);

    for (int i = 0; i < PUSHING_THREAD_COUNT; i++) {
      Thread thread = new Thread(() -> {
        while (true) {
          stack.push(ThreadLocalRandom.current().nextInt());
        }
      });
      thread.setDaemon(true);
      threads.add(thread);
    }
    for (int i = 0; i < POPPING_THREAD_COUNT; i++) {
      Thread thread = new Thread(() -> {
        while (true) {
          stack.pop();
        }
      });
      thread.setDaemon(true);
      threads.add(thread);
    }

    threads.forEach(Thread::start);

    Thread.sleep(10000);
    System.out.println(String.format("Number of operations in 10 seconds: %,d", stack.getCounter()));
  }

  public static class LockFreeStack<T> {
    private AtomicReference<StackNode<T>> head = new AtomicReference<>();
    private AtomicInteger counter = new AtomicInteger(0);

    public void push(T value) {
      StackNode<T> newHead = new StackNode<>(value);
      while (true) {
        StackNode<T> currentHead = head.get();
        newHead.next = currentHead;
        if (head.compareAndSet(currentHead, newHead)) {
          break;
        } else {
          LockSupport.parkNanos(1);
        }
      }
      counter.incrementAndGet();
    }

    public T pop() {
      StackNode<T> currentHead = head.get();
      StackNode<T> newHead;

      while (Objects.nonNull(currentHead)) {
        newHead = currentHead.next;
        if (head.compareAndSet(currentHead, newHead)) {
          break;
        } else {
          LockSupport.parkNanos(1);
          currentHead = head.get();
        }
      }
      counter.incrementAndGet();
      return Objects.nonNull(currentHead) ? currentHead.value : null;
    }

    public int getCounter() {
      return counter.get();
    }
  }

  private static class LockStack<T> {
    private StackNode<T> head;
    private int counter = 0;

    public synchronized void push(T value) {
      StackNode<T> newHead = new StackNode<>(value);
      newHead.next = head;
      head = newHead;
      counter++;
    }

    public synchronized T pop() {
      if (Objects.isNull(head)) {
        counter++;
        return null;
      }

      T value = head.value;
      head = head.next;
      counter++;
      return value;
    }

    public int getCounter() {
      return counter;
    }
  }

  private static class StackNode<T> {
    public T value;
    public StackNode<T> next;

    public StackNode(T value) {
      this.value = value;
    }
  }
}
