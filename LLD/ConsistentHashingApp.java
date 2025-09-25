package LLD;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/* 
 * Consistent hashing is a hashing scheme that minimizes key remapping when nodes are added or removed.
 * 
 * Key Concepts:
    Hash Ring: Imagine hash values laid out in a circular space (0 to 2³² - 1).
    Nodes: Servers are hashed into positions on the ring.
    Keys: Data keys are also hashed into positions.
    A key belongs to the first node that appears clockwise on the ring.
 */

class ConsistentHashing {
   private final int replicas;
   private final SortedMap<Integer, String> circle = new TreeMap<>();

   public ConsistentHashing(int replicas, Collection<String> nodes) {
      this.replicas = replicas;
      for (String node : nodes) {
         addNode(node);
      }
   }

   private void addNode(String node) {
      for (int i = 0; i < replicas; i++) {
         int hash = getHash(node + i);
         circle.put(hash, node);
      }
   }

   public void removeNode(String node) {
      for (int i = 0; i < replicas; i++) {
         int hash = getHash(node + i);
         circle.remove(hash);
      }
   }

   private int getHash(String key) {
      return key.hashCode() & 0x7fffffff;
   }

   public String getNode(String key) {
      if (circle.isEmpty()) {
         return null;
      }
      int hash = getHash(key);
      if (!circle.containsKey(hash)) {
         SortedMap<Integer, String> tailMap = circle.tailMap(hash);
         hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
      }
      return circle.get(hash);
   }

   public void printRing() {
      for (Map.Entry<Integer, String> entry : circle.entrySet()) {
         System.out.println("Hash: " + entry.getKey() + " -> Node: " + entry.getValue());
      }
   }
}

public class ConsistentHashingApp {
   public static void main(String[] a) {
      List<String> nodes = Arrays.asList("A", "B");
      ConsistentHashing ch = new ConsistentHashing(3, nodes);

      ch.printRing();

      System.out.println("Key1 is mapped to: " + ch.getNode("Key1"));
      System.out.println("Key2 is mapped to: " + ch.getNode("Key2"));

      System.out.println("\nRemoving NodeB...");
      ch.removeNode("NodeB");

      ch.printRing();
      System.out.println("Key1 is now mapped to: " + ch.getNode("Key1"));
      System.out.println("Key2 is now mapped to: " + ch.getNode("Key2"));
   }
}
