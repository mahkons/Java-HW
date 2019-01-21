package ru.hse.kostya.java.hashtable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    private HashTable t;

    @BeforeEach
    void initHashTable() {
        t = new HashTable();
    }

    @Test
    void size() {
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
        assertFalse(t.contains("a"));
        t.put("a", "b");
        assertTrue(t.contains("a"));
    }

    @Test
    void get() {
        assertNull(t.get("a"));
        t.put("a", "b");
        assertNotNull(t.get("a"));
        assertEquals("b", t.get("a"));
    }

    @Test
    void put() {
        t.put("a", "b");
        assertEquals("b", t.get("a"));
        assertEquals("b", t.put("a", "c"));
        assertEquals("c", t.get("a"));
    }

    @Test
    void remove() {
        t.put("a", "b");
        assertNull(t.remove("b"));
        assertEquals("b", t.remove("a"));
        assertNull(t.get("a"));
    }

    @Test
    void clear() {
        t.put("a", "b");
        t.clear();
        assertTrue(t.size() == 0);
    }
}