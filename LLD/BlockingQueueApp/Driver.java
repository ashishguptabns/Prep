package LLD.BlockingQueueApp;

public class Driver {

    public static void main(String[] a) throws Exception {
        CustomBlockingQueue<String> q = new CustomBlockingQueue<>(2);
        int count = 10;
        while (count-- > 0) {
            Thread t = new Thread(() -> {
                try {
                    q.offer("Hello");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            t.start();
        }
    }

}
