package ru.hse.kostya.java.hashtable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListTest {

    private List l;

    @BeforeEach
    void initList() {
        l = new List();
    }

    @Test
    void size() {
        assertEquals(0, l.size());
        l.put("a", "b");
        assertEquals(1, l.size());
        l.put("b", "c");
        assertEquals(2, l.size());
        l.put("a", "c");
        assertEquals(2, l.size());
    }

    @Test
    void empty() {
        assertTrue(l.empty());
        l.put("a", "b");
        assertFalse(l.empty());
    }

    @Test
    void contains() {
        assertFalse(l.contains("a"));
        l.put("a", "b");
        assertTrue(l.contains("a"));
    }

    @Test
    void get() {
        assertNull(l.get("a"));
        l.put("a", "b");
        assertNotNull(l.get("a"));
        assertEquals("b", l.get("a"));
    }

    @Test
    void put() {
        l.put("a", "b");
        assertNotNull(l.get("a"));
        assertEquals("b", l.get("a"));

        assertEquals("b", l.put("a", "c"));
        assertNotNull(l.get("a"));
        assertEquals("c", l.get("a"));
    }

    @Test
    void remove() {
        l.put("a", "b");
        l.put("b", "c");
        l.remove("b");
        assertNotNull(l.get("a"));
        assertEquals("b", l.get("a"));
        assertNull(l.get("b"));
    }

    @Test
    void popHeadElement() {
        assertNull(l.popHeadElement());
        l.put("a", "b");
        assertEquals("a", l.popHeadElement().getKey());
        assertNull(l.popHeadElement());
    }

    @Test
    void clear() {
        assertTrue(l.empty());
        l.put("a", "b");
        l.clear();
        assertTrue(l.empty());
    }
}