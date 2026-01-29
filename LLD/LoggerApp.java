package LLD;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

class LogMessage {
    private final String msg;
    private final long ts;

    public LogMessage(String msg) {
        this.msg = msg;
        this.ts = System.currentTimeMillis();
    }

    public String format() {
        return this.ts + " - " + msg;
    }
}

interface ILogger {
    public void log(LogMessage msg);
}

class LogWriter {
    private BufferedWriter writer = null;

    public LogWriter(String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName, true);
            writer = new BufferedWriter(new OutputStreamWriter(fos));
        } catch (FileNotFoundException e) {
        }
    }

    public synchronized void write(LogMessage msg) {
        try {
            writer.write(msg.format());
            writer.newLine();
            writer.flush();
        } catch (IOException ex) {
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
        }
    }
}

class LogWorker extends Thread {
    private final BlockingDeque<LogMessage> queue;
    private volatile boolean running = true;
    private final LogWriter writer = new LogWriter("log_file.log");

    public LogWorker(BlockingDeque<LogMessage> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (this.running || !queue.isEmpty()) {
            try {
                LogMessage msg = queue.poll(1, TimeUnit.SECONDS);
                if (msg != null) {
                    writer.write(msg);
                }
            } catch (Exception e) {
            }
        }
        writer.close();
    }

    public void shutDown() {
        this.running = false;
        this.interrupt();
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
