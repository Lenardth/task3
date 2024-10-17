import java.lang.Thread;
import java.util.ArrayList;
import java.util.Random;

// Fifo.java
class Fifo {
    private final ArrayList<String> queue = new ArrayList<>();
    private final int capacity = 10;

    public synchronized void put(String value) throws InterruptedException {
        System.out.println("put method called by thread ID: " + Thread.currentThread().getId());
        while (queue.size() == capacity) {
            wait(); // Wait if the queue is full
        }
        queue.add(value);
        notifyAll(); // Notify any waiting threads
    }

    public synchronized String get() throws InterruptedException {
        System.out.println("get method called by thread ID: " + Thread.currentThread().getId());
        while (queue.isEmpty()) {
            wait(); // Wait if the queue is empty
        }
        String value = queue.remove(0);
        notifyAll(); // Notify any waiting threads
        return value;
    }
}

// Producer.java
class Producer implements Runnable {
    private String text;
    private int messageNumber;
    private Fifo fifo;
    private int waitTime;

    public Producer(String text, Fifo fifo, int waitTime) {
        this.text = text;
        this.messageNumber = 0;
        this.fifo = fifo;
        this.waitTime = waitTime;
    }

    @Override
    public void run() {
        while (true) {
            go();
            try {
                Thread.sleep(waitTime); // Sleep for the specified waiting time
            } catch (InterruptedException e) {
                e.printStackTrace();
                break; // Stop the thread if it is interrupted
            }
        }
    }

    public void go() {
        long currentTime = System.currentTimeMillis() % 100000; // Get the last 5 digits of system time in milliseconds
        String message = text + " " + messageNumber;
        try {
            fifo.put(message);
            System.out.println("produced " + message + " " + currentTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        messageNumber++;
    }
}

// Consumer.java
class Consumer implements Runnable {
    private Fifo fifo;
    private String consumerName;
    private int delay;

    public Consumer(Fifo fifo, String consumerName, int delay) {
        this.fifo = fifo;
        this.consumerName = consumerName;
        this.delay = delay;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = fifo.get();
                long currentTime = System.currentTimeMillis() % 100000;
                System.out.println("consumed " + consumerName + " " + message + " " + currentTime);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break; // Stop the thread if it is interrupted
            }
        }
    }
}

// Application.java
public class Application {
    public static void main(String[] args) {
        Fifo fifo = new Fifo();
        Random random = new Random();

        // Create multiple producers and consumers with random waiting times
        Thread producer1 = new Thread(new Producer("first", fifo, 1000));
        Thread producer2 = new Thread(new Producer("second", fifo, 1200));
        Thread producer3 = new Thread(new Producer("third", fifo, 1500));

        Thread consumer1 = new Thread(new Consumer(fifo, "consumer1", 500));
        Thread consumer2 = new Thread(new Consumer(fifo, "consumer2", 700));
        Thread consumer3 = new Thread(new Consumer(fifo, "consumer3", 800));
        Thread consumer4 = new Thread(new Consumer(fifo, "consumer4", 600));

        producer1.start();
        producer2.start();
        producer3.start();

        consumer1.start();
        consumer2.start();
        consumer3.start();
        consumer4.start();
    }
}
