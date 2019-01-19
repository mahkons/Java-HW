package hashtable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListTest {

    @Test
    void size() {
        List l = new List();
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
        List l = new List();
        assertTrue(l.empty());
        l.put("a", "b");
        assertFalse(l.empty());
    }

    @Test
    void contains() {
        List l = new List();
        assertFalse(l.contains("a"));
        l.put("a", "b");
        assertTrue(l.contains("a"));
    }

    @Test
    void get() {
        List l = new List();
        assertNull(l.get("a"));
        l.put("a", "b");
        assertNotNull(l.get("a"));
        assertEquals("b", l.get("a"));
    }

    @Test
    void put() {
        List l = new List();
        l.put("a", "b");
        assertNotNull(l.get("a"));
        assertEquals("b", l.get("a"));

        assertEquals("b", l.put("a", "c"));
        assertNotNull(l.get("a"));
        assertEquals("c", l.get("a"));
    }

    @Test
    void remove() {
        List l = new List();
        l.put("a", "b");
        l.put("b", "c");
        l.remove("b");
        assertNotNull(l.get("a"));
        assertEquals("b", l.get("a"));
        assertNull(l.get("b"));
    }

    @Test
    void popHeadElement() {
        List l = new List();
        assertNull(l.popHeadElement());
        l.put("a", "b");
        assertEquals("a", l.popHeadElement().getKey());
        assertNull(l.popHeadElement());
    }

    @Test
    void clear() {
        List l = new List();
        assertTrue(l.empty());
        l.put("a", "b");
        l.clear();
        assertTrue(l.empty());
    }
}