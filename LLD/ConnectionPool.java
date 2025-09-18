package LLD;

import java.util.ArrayList;
import java.util.List;

/*
Key Points to Cover:

Maintain a fixed-size pool of DB connections.
Provide getConnection() and releaseConnection() methods.
Use blocking/waiting if no connections available.
Thread-safe (synchronized/locks).
Optional: connection timeout, health checks, singleton pattern.
*/
public class ConnectionPool<T> {
    int size;
    List<T> pool = new ArrayList<>();

    public ConnectionPool(int size) {
        this.size = size;
    }

    private synchronized Connection getConnection() throws Exception {
        while (pool.size() == 0) {
            System.out.println("Waiting to get - " + Thread.currentThread().getName());
            wait();
        }
        Connection c = (Connection) pool.remove(0);
        System.out.println("Got - " + c.getName());
        notifyAll();
        return c;
    }

    private synchronized void addConnection(T connection) throws Exception {
        while (pool.size() >= this.size) {
            System.out.println("Waiting to add - " + Thread.currentThread().getName());
            wait();
        }
        System.out.println("Added - " + Thread.currentThread().getName());
        pool.add(connection);
        notifyAll();
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
                        System.out.println(
                                "connection - " + c.getName() + " thread - " + Thread.currentThread().getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
            t.start();
        }
    }
}
