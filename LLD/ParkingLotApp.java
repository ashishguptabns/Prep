package LLD;

/* 
 * Multiple entry gates, levels and spots
 * 
 * Use WAL with blocking queue for recovery
 * -    dedicated thread to flush to disk
 * 
 * Write critical events synchronously
 * 
 * Use ConcurrentSkipListSet to store available spots
 * -    thread safe and O(log n) look up
 * 
 * Atomic Integer per level to track level capacity
 */
public class ParkingLotApp {
}