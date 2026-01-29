package LLD;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*
Blocking Queue

Key Operations:

enqueue(item) – Adds item; blocks if full.
dequeue() – Removes and returns item; blocks if empty.
size() – Returns current queue size.
*/

public class BlockingQueue<T> {
    int size = 0;
    int max = 0;
    List<T> q = new LinkedList<>();

    public BlockingQueue(int max) {
        this.max = max;
    }

    private synchronized void offer(T value) throws Exception {
        while (this.size >= this.max) {
            System.out.println("Waiting - " + value + " on thread - " + Thread.currentThread().getName());
            wait();
        }
        this.size++;
        q.add(value);
        System.out.println("Added - " + value);
        notifyAll();
    }

    public static void main(String[] a) throws Exception {
        BlockingQueue<String> q = new BlockingQueue<>(2);
        int count = 10;
        while (count-- > 0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        q.offer("Hello");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }
}
