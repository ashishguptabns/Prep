package LLD.BlockingQueueApp;

import java.util.LinkedList;
import java.util.List;

public class CustomBlockingQueue<T> {

    int size = 0;
    int max = 0;
    List<T> q = new LinkedList<>();

    public CustomBlockingQueue(int max) {
        this.max = max;
    }

    public synchronized void offer(T value) throws Exception {
        while (this.size >= this.max) {
            System.out.println("Waiting - " + value + " on thread - " + Thread.currentThread().getName());
            wait();
        }
        this.size++;
        q.add(value);
        System.out.println("Added - " + value);
        notifyAll();
    }

}
