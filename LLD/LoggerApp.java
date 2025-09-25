package LLD;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

class LogMessage {
    private final String msg;

    public LogMessage(String msg) {
        this.msg = msg;
    }
}

interface ILogger {
    public void log(LogMessage msg);
}

class LogWorker extends Thread {
    private final BlockingDeque<LogMessage> queue;
    private volatile boolean running = true;

    public LogWorker(BlockingDeque<LogMessage> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (this.running || !queue.isEmpty()) {
            
        }
    }
}

class Logger implements ILogger {
    private static final Logger instance = new Logger();
    private final BlockingDeque<LogMessage> queue = new LinkedBlockingDeque<>();
    private final LogWorker worker;

    static Logger getInstance() {
        return instance;
    }

    private Logger() {
        worker = new LogWorker(queue);
        worker.start();
    }

    @Override
    public void log(LogMessage msg) {
        queue.offer(msg);
    }
}

public class LoggerApp {
    public static void main(String[] a) {
        ILogger logger = Logger.getInstance();
        int count = 1000;
        while (count-- > 0) {
            final int countA = count;
            Thread t = new Thread(() -> {
                logger.log(new LogMessage(countA + " " + Thread.currentThread().getName()));
            });
            t.start();
        }
    }
}
