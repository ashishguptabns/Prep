package LLD;

import java.util.ArrayList;
import java.util.List;

public class BlockingQueue<T> {
    int size = 0;
    int max = 0;
    List<T> q = new ArrayList<>();

    public BlockingQueue(int max) {
        this.max = max;
    }

    private synchronized void offer(T value) throws Exception {
        while (this.size >= this.max) {
            System.out.println("Waiting - " + value);
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
