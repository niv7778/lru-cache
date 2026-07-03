package com.example.lru;

/** Demonstrates basic LRUCache usage. Run with: java -cp target/classes com.example.lru.Main */
public class Main {
    public static void main(String[] args) {
        LRUCache<Integer, String> cache = new LRUCache<>(3);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");
        System.out.println("After inserting 1,2,3: " + cache);
        // -> [3, 2, 1]  (most to least recently used)

        cache.get(1); // touch key 1, making it most recently used
        System.out.println("After get(1): " + cache);
        // -> [1, 3, 2]

        cache.put(4, "four"); // capacity exceeded -> evicts least recently used (2)
        System.out.println("After put(4, ...) (evicts LRU key 2): " + cache);
        // -> [4, 1, 3]

        System.out.println("get(2) -> " + cache.get(2)); // null, was evicted
        System.out.println("get(3) -> " + cache.get(3)); // "three"
    }
}
