package LLD;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/*
Problem Statement:

Design a system like Google Docs that allows multiple users to edit a shared document concurrently with the following requirements:

💡 Key Requirements:

Multiple users can edit the same document simultaneously.
Changes are merged in real-time and visible to all collaborators.
Handle conflicts and concurrent edits gracefully.
Support undo/redo operations per user.
Support offline edits and syncing when back online.
Versioning of the document with the ability to revert to any previous version.
System should scale to millions of documents and thousands of concurrent users per document.
*/

class Worker extends Thread {
    final BlockingDeque<Change> queue;
    volatile boolean isActive = true;
    final Editor editor;

    public Worker(BlockingDeque<Change> queue, Editor editor) {
        this.editor = editor;
        this.queue = queue;
    }

    @Override
    public void run() {
        while (isActive || !queue.isEmpty()) {
            try {
                Change ch = queue.poll(100, TimeUnit.MILLISECONDS);
                if (ch != null) {
                    System.out.println(ch.val);
                    editor.makeChange(ch);
                } else {
                    System.out.println("Queue is empty");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutDown() {
        this.isActive = false;
        interrupt();
    }
}

class Editor {
    Worker worker;
    final BlockingDeque<Change> queue = new LinkedBlockingDeque<>();
    final StringBuilder doc = new StringBuilder();

    public Editor() {
        worker = new Worker(queue, this);
        worker.start();
    }

    void makeChange(Change ch) {
        if (ch.type == Change.TYPE.INSERT) {
            int index = Math.max(0, Math.min(ch.index, doc.length()));
            doc.insert(index, ch.val);

        }
        System.out.println("Document after change: " + doc);
    }

    void queueChange(Change ch) {
        queue.offer(ch);
    }

    public void shutDown() {
        worker.shutDown();
    }
}

class Change {
    enum TYPE {
        INSERT
    }

    final String val;
    final TYPE type;
    final int index;

    public Change(String val, TYPE type, int index) {
        this.val = val;
        this.type = type;
        this.index = index;
    }
}

public class EditorApp {

    public static void main(String[] a) throws InterruptedException {
        Editor editor = new Editor();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(100);
        int count = 100;
        while (count-- > 0) {
            try {
                final int index = count;
                executor.submit(() -> {
                    Change ch = new Change("op-" + index, Change.TYPE.INSERT, 0);
                    editor.queueChange(ch);
                });
            } finally {
                latch.countDown();
            }
        }

        latch.await();
        executor.shutdown();
    }
}
