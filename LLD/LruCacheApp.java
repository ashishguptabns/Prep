package LLD;

/*
Problem Statement:

Design a distributed in-memory key-value cache that supports:
High throughput and low latency.
Data sharding across multiple nodes.
Data replication for fault tolerance.
Cache eviction policies (LRU, LFU).
Expiry of cache entries (TTL).
Strong or eventual consistency options.
Support for atomic operations (e.g., increment, compare-and-set).

💡 Key Requirements:
Interface design for cache clients.
How to shard and replicate data across nodes.
Design cache eviction mechanisms efficiently.
Handle node failures and data recovery.
Support consistency models and concurrency control.
Support TTL and automatic expiry cleanup.
Extensibility to support different data types or modules.
*/

enum ConsistencyLevel {
    STRONG, EVENTUAL
}

interface Cache {
    String get(String key);

    void put(String key, String value);

    void put(String key, String value, long ttl);

    void delete(String key);

    boolean compareAndSet(String key, String expectedValue, String newValue);

    long increment(String key);

    void setConsistencyLevel(ConsistencyLevel level);
}

public class LruCacheApp {

}
