package LLD;
/*
Problem Statement:

Design a system that can apply rate limiting to APIs exposed by a backend service. The system should support multiple rate limiting strategies:

Fixed Window
Sliding Window Log
Sliding Window Counter
Token Bucket
Leaky Bucket
*/

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiter {

    ReqHandler reqHandler;
    Storage storage;

    public RateLimiter() {
        reqHandler = new ReqHandler();
        storage = new Storage();
    }

    void run() {
        Request req = new Request("A");
        int count = 20;
        while (count-- > 0) {
            final String newI = count + "";
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    reqHandler.handleRequest(newI + "", req);
                }
            });
            thread.start();
        }
    }

    public static void main(String[] args) {
        RateLimiter app = new RateLimiter();
        app.run();
    }

    class ReqHandler {
        void handleRequest(String index, Request req) {
            if (isAllowed(req)) {
                System.out.println("Req allowed: " + req + " " + index);
            } else {
                System.out.println("Req not allowed: " + req + " " + index);
            }
        }
    }

    public synchronized boolean isAllowed(Request req) {
        if (storage.getReqCount(req.type) < storage.getRateLimit(req.type)) {
            storage.incrementRateCount(req.type);
            System.out.println("Req count: " + req.type + " " + storage.getReqCount(req.type) + " " + storage
                    .getRateLimit(req.type));
            return true;
        }
        return false;
    }

    class Storage {
        ConcurrentHashMap<String, AtomicInteger> reqCountMap;
        Map<String, Integer> rateLimitMap;

        public Storage() {
            reqCountMap = new ConcurrentHashMap<>();
            rateLimitMap = new HashMap<>();
        }

        void incrementRateCount(String type) {
            reqCountMap.computeIfAbsent(type, k -> new AtomicInteger(0)).incrementAndGet();
        }

        int getReqCount(String type) {
            return reqCountMap.getOrDefault(type, new AtomicInteger(0)).get();
        }

        int getRateLimit(String type) {
            return rateLimitMap.getOrDefault(type, 10);
        }
    }

    class Request {
        String type;

        public Request(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }
}
