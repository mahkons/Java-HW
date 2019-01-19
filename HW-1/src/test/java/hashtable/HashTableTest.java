package hashtable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    @Test
    void size() {
        HashTable t = new HashTable();
        assertEquals(0, t.size());
        t.put("a", "b");
        assertEquals(1, t.size());
        t.put("a", "c");
        assertEquals(1, t.size());
        t.remove("a");
        assertEquals(0, t.size());
    }

    @Test
    void contains() {
        HashTable t = new HashTable();
        assertFalse(t.contains("a"));
        t.put("a", "b");
        assertTrue(t.contains("a"));
    }

    @Test
    void get() {
        HashTable t = new HashTable();
        assertNull(t.get("a"));
        t.put("a", "b");
        assertNotNull(t.get("a"));
        assertEquals("b", t.get("a"));
    }

    @Test
    void put() {
        HashTable t = new HashTable();
        t.put("a", "b");
        assertEquals("b", t.get("a"));
        assertEquals("b", t.put("a", "c"));
        assertEquals("c", t.get("a"));
    }

    @Test
    void remove() {
        HashTable t = new HashTable();
        t.put("a", "b");
        assertNull(t.remove("b"));
        assertEquals("b", t.remove("a"));
        assertNull(t.get("a"));
    }

    @Test
    void clear() {
        HashTable t = new HashTable();
        t.put("a", "b");
        t.clear();
        assertTrue(t.size() == 0);
    }
}