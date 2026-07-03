package com.example.lru;

import java.util.HashMap;
import java.util.Map;

/**
 * A generic Least Recently Used (LRU) cache.
 *
 * <p>Supports O(1) average-time {@link #get(Object)} and {@link #put(Object, Object)}
 * operations by combining a {@link HashMap} (for O(1) key lookup) with an
 * intrusive doubly linked list (for O(1) reordering / eviction).
 *
 * <p>Most-recently-used entries live near the {@code head} sentinel; the
 * least-recently-used entry sits just before the {@code tail} sentinel and is
 * the one evicted when the cache exceeds its capacity.
 *
 * <p>All public methods are synchronized, making this implementation
 * thread-safe for concurrent access.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class LRUCache<K, V> {

    /** Node of the intrusive doubly linked list. */
    private class Node {
        K key;
        V value;
        Node prev;
        Node next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node> map;

    // Sentinel nodes simplify insert/remove logic (no null checks at the ends).
    private final Node head; // head.next = most recently used
    private final Node tail; // tail.prev = least recently used

    /**
     * Creates an LRU cache with the given capacity.
     *
     * @param capacity maximum number of entries the cache may hold; must be &gt; 0
     * @throws IllegalArgumentException if capacity is not positive
     */
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive, got: " + capacity);
        }
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node(null, null);
        this.tail = new Node(null, null);
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Returns the value for {@code key}, marking it as most recently used,
     * or {@code null} if absent.
     */
    public synchronized V get(K key) {
        Node node = map.get(key);
        if (node == null) {
            return null;
        }
        moveToFront(node);
        return node.value;
    }

    /**
     * Inserts or updates {@code key} with {@code value}, marking it as most
     * recently used. If the cache is over capacity afterwards, evicts the
     * least recently used entry.
     */
    public synchronized void put(K key, V value) {
        Node existing = map.get(key);
        if (existing != null) {
            existing.value = value;
            moveToFront(existing);
            return;
        }

        Node node = new Node(key, value);
        map.put(key, node);
        addToFront(node);

        if (map.size() > capacity) {
            Node lru = tail.prev;
            removeNode(lru);
            map.remove(lru.key);
        }
    }

    /** Removes {@code key} from the cache if present. Returns the removed value, or null. */
    public synchronized V remove(K key) {
        Node node = map.remove(key);
        if (node == null) {
            return null;
        }
        removeNode(node);
        return node.value;
    }

    /** Returns true if {@code key} is currently in the cache (does not affect recency). */
    public synchronized boolean containsKey(K key) {
        return map.containsKey(key);
    }

    /** Current number of entries in the cache. */
    public synchronized int size() {
        return map.size();
    }

    /** Maximum number of entries this cache can hold. */
    public int capacity() {
        return capacity;
    }

    /** Removes all entries from the cache. */
    public synchronized void clear() {
        map.clear();
        head.next = tail;
        tail.prev = head;
    }

    // -- internal doubly linked list helpers --

    private void addToFront(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToFront(Node node) {
        removeNode(node);
        addToFront(node);
    }

    /** Returns keys ordered from most recently used to least recently used. Useful for debugging/tests. */
    public synchronized java.util.List<K> keysMostToLeastRecentlyUsed() {
        java.util.List<K> keys = new java.util.ArrayList<>();
        Node current = head.next;
        while (current != tail) {
            keys.add(current.key);
            current = current.next;
        }
        return keys;
    }

    @Override
    public synchronized String toString() {
        return "LRUCache" + keysMostToLeastRecentlyUsed();
    }
}
