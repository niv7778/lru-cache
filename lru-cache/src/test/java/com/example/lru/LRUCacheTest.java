package com.example.lru;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LRUCacheTest {

    @Test
    void putAndGetBasic() {
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        assertEquals(1, cache.get("a"));
        assertEquals(2, cache.get("b"));
    }

    @Test
    void getMissingKeyReturnsNull() {
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        assertNull(cache.get("missing"));
    }

    @Test
    void evictsLeastRecentlyUsedWhenOverCapacity() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three"); // evicts 1, since it's LRU

        assertNull(cache.get(1));
        assertEquals("two", cache.get(2));
        assertEquals("three", cache.get(3));
        assertEquals(2, cache.size());
    }

    @Test
    void getRefreshesRecency() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "one");
        cache.put(2, "two");
        cache.get(1);          // 1 is now most recently used
        cache.put(3, "three"); // should evict 2, not 1

        assertEquals("one", cache.get(1));
        assertNull(cache.get(2));
        assertEquals("three", cache.get(3));
    }

    @Test
    void putOnExistingKeyUpdatesValueAndRecency() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(1, "ONE-updated"); // updates value, 1 becomes most recently used
        cache.put(3, "three");       // should evict 2

        assertEquals("ONE-updated", cache.get(1));
        assertNull(cache.get(2));
        assertEquals("three", cache.get(3));
    }

    @Test
    void removeDeletesEntry() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "one");
        cache.put(2, "two");

        assertEquals("one", cache.remove(1));
        assertNull(cache.get(1));
        assertEquals(1, cache.size());
        assertNull(cache.remove(1)); // already removed
    }

    @Test
    void containsKeyDoesNotAffectRecency() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "one");
        cache.put(2, "two");

        assertTrue(cache.containsKey(1));
        cache.put(3, "three"); // containsKey should NOT have refreshed 1's recency, so 1 is evicted

        assertNull(cache.get(1));
        assertEquals("three", cache.get(3));
    }

    @Test
    void clearEmptiesCache() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "one");
        cache.put(2, "two");
        cache.clear();

        assertEquals(0, cache.size());
        assertNull(cache.get(1));
        assertNull(cache.get(2));
    }

    @Test
    void capacityOneWorksCorrectly() {
        LRUCache<Integer, String> cache = new LRUCache<>(1);
        cache.put(1, "one");
        cache.put(2, "two"); // evicts 1 immediately

        assertNull(cache.get(1));
        assertEquals("two", cache.get(2));
        assertEquals(1, cache.size());
    }

    @Test
    void constructorRejectsNonPositiveCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new LRUCache<Integer, String>(0));
        assertThrows(IllegalArgumentException.class, () -> new LRUCache<Integer, String>(-5));
    }

    @Test
    void recencyOrderMatchesExpectedSequence() {
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");
        cache.get(1);

        List<Integer> order = cache.keysMostToLeastRecentlyUsed();
        assertEquals(List.of(1, 3, 2), order);
    }
}
