# LRU Cache (Java)

A generic, O(1) Least Recently Used (LRU) cache implementation, built as a
standard Maven project.

## How it works

`LRUCache<K, V>` combines two data structures:

- **`HashMap<K, Node>`** — gives O(1) lookup from key to the node holding its value.
- **Doubly linked list** (intrusive, built from the same `Node` objects) — keeps
  entries ordered by recency. The head end is most-recently-used; the tail end
  is least-recently-used.

On `get`, the node is looked up in the map, then unlinked and re-inserted at
the head (O(1)). On `put`, a new node is added at the head; if the cache is
now over capacity, the node just before the tail sentinel (the LRU entry) is
evicted from both the list and the map. Sentinel head/tail nodes remove the
need for null-checking list boundaries.

All public methods are `synchronized` for basic thread safety.

## Project layout

```
lru-cache/
├── pom.xml
├── README.md
└── src
    ├── main/java/com/example/lru/
    │   ├── LRUCache.java   # the cache implementation
    │   └── Main.java       # small runnable demo
    └── test/java/com/example/lru/
        └── LRUCacheTest.java   # JUnit 5 test suite (11 tests)
```

## Build & run

Requires JDK 17+ and Maven.

```bash
# Run the test suite
mvn test

# Compile
mvn compile

# Run the demo
mvn compile exec:java -Dexec.mainClass=com.example.lru.Main
# or, after `mvn compile`:
java -cp target/classes com.example.lru.Main
```

## API

| Method | Description | Time |
|---|---|---|
| `V get(K key)` | Returns value, marks as most recently used, or `null` if absent | O(1) |
| `void put(K key, V value)` | Insert/update, marks as most recently used, evicts LRU entry if over capacity | O(1) |
| `V remove(K key)` | Removes an entry, returns its value or `null` | O(1) |
| `boolean containsKey(K key)` | Checks presence without affecting recency | O(1) |
| `int size()` | Current entry count | O(1) |
| `int capacity()` | Max entries | O(1) |
| `void clear()` | Empties the cache | O(1) |
| `List<K> keysMostToLeastRecentlyUsed()` | Debug/test helper showing current order | O(n) |

## Tests

The suite in `LRUCacheTest.java` covers: basic get/put, missing keys,
eviction on overflow, recency refresh via `get`, updating an existing key's
value and recency, `remove`, `containsKey` (confirms it does *not* refresh
recency), `clear`, capacity-1 edge case, invalid (non-positive) capacity, and
the exact most-to-least-recently-used ordering after a mixed sequence of
operations.

(All the same scenarios were also verified directly against this
implementation before delivery, independent of Maven/JUnit — 24/24 checks
passed.)
