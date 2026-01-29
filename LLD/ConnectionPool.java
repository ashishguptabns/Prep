package LLD;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConnectionPool<T> {
    final java.util.concurrent.BlockingQueue<T> pool;

    public ConnectionPool(int size) {
        pool = new LinkedBlockingQueue<>(size);
    }

    private T getConnection() throws Exception {
        T c = pool.poll(200, TimeUnit.MILLISECONDS);
        System.out.println("Got - " + ((Connection) c).getName());
        return c;
    }

    private void addConnection(T connection) {
        if (connection != null) {
            pool.offer(connection);
        }
    }

    static class Connection {
        String name;

        public Connection(String name) {
            this.name = name;
        }

        String getName() {
            return this.name;
        }
    }

    public static void main(String[] a) {
        ConnectionPool<Connection> pool = new ConnectionPool<>(3);
        int count = 10;
        while (count-- > 0) {
            Thread t1 = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Connection c = new Connection(Thread.currentThread().getName());
                        pool.addConnection(c);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
            t1.start();

            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Connection c = pool.getConnection();
                        try {
                            System.out.println(
                                    "connection - " + c.getName() + " thread - " + Thread.currentThread().getName());
                        } finally {
                            pool.addConnection(c);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
            t.start();
        }
    }
}
