package LLD;

class Entry<K, V> {
    K key;
    V val;
    Entry<K, V> next;

    public Entry(K key, V val) {
        this.key = key;
        this.val = val;
    }
}

public class MyHashMap<K, V> {
    Entry<K, V>[] buckets;
    int size = 10;

    public MyHashMap() {
        buckets = new Entry[this.size];
    }

    public V get(K key) {
        int index = getBucketIndex(key);
        Entry<K, V> head = buckets[index];

        while (head != null) {
            if (head.key.equals(key)) {
                return head.val;
            }
            head = head.next;
        }

        return null;
    }

    public V remove(K key) {
        return null;
    }

    public void put(K key, V val) {
        Entry<K, V> head = buckets[getBucketIndex(key)];
        while (head != null) {
            if (head.key.equals(key)) {
                head.val = val;
                return;
            }
            head = head.next;
        }
        Entry<K, V> node = new Entry(key, val);
        node.next = buckets[getBucketIndex(key)];
        buckets[getBucketIndex(key)] = node;
    }

    private int getBucketIndex(K key) {
        return Math.abs(key.hashCode()) % buckets.length;
    }

    public static void main(String[] s) {
        MyHashMap<String, String> map = new MyHashMap<>();
        System.out.println(map.get("xyz"));
        map.put("xyz", "xyz");
        System.out.println(map.get("xyz"));
    }
}
