package ru.hse.kostya.java;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleMyTreeSetTest {

    private SimpleMyTreeSet<Object> emptyTreeSet = new SimpleMyTreeSet<>();
    private SimpleMyTreeSet<String> simpleSetNoComparator = new SimpleMyTreeSet<>();
    private SimpleMyTreeSet<String> simpleSetWithComparator = new SimpleMyTreeSet<>(String.CASE_INSENSITIVE_ORDER);

    @BeforeEach
    void setUp() {
        simpleSetNoComparator.add("aaa");
        simpleSetNoComparator.add("AAA");
        simpleSetNoComparator.add("BBB");

        simpleSetWithComparator.add("aaa");
        simpleSetWithComparator.add("AAA");
        simpleSetWithComparator.add("BBB");
    }

    @Test
    void size() {
        assertEquals(0, emptyTreeSet.size());
        assertEquals(3, simpleSetNoComparator.size());
    }

    @Test
    void iterator() {
    }

    @Test
    void descendingIterator() {
    }

    @Test
    void descendingSet() {
    }

    @Test
    void contains() {
        assertFalse(emptyTreeSet.contains("aaa"));
        assertFalse(simpleSetNoComparator.contains("aaA"));
        assertTrue(simpleSetNoComparator.contains("AAA"));
    }

    @Test
    void add() {
        assertTrue(simpleSetNoComparator.add("bbb"));
        assertEquals("bbb", simpleSetNoComparator.last());
        assertFalse(simpleSetNoComparator.add("aaa"));
    }

    @Test
    void remove() {
        assertTrue(simpleSetNoComparator.remove("aaa"));
        assertEquals("BBB", simpleSetNoComparator.last());
        assertFalse(simpleSetNoComparator.remove("bbb"));
    }

    @Test
    void first() {
        assertEquals("AAA", simpleSetNoComparator.first());
        simpleSetNoComparator.remove("AAA");
        assertEquals("BBB", simpleSetNoComparator.first());
    }

    @Test
    void last() {
        assertEquals("aaa", simpleSetNoComparator.last());
        simpleSetNoComparator.remove("aaa");
        assertEquals("BBB", simpleSetNoComparator.last());
    }

    @Test
    void lower() {
        assertEquals("AAA", simpleSetNoComparator.lower("BBB"));
        assertNull(simpleSetNoComparator.lower("AAA"));
        assertEquals("aaa", simpleSetNoComparator.lower("zzz"));
    }

    @Test
    void floor() {
        assertEquals("BBB", simpleSetNoComparator.floor("BBB"));
        assertNull(simpleSetNoComparator.floor("A"));
        assertEquals("aaa", simpleSetNoComparator.floor("zzz"));
    }

    @Test
    void higher() {
        assertEquals("aaa", simpleSetNoComparator.higher("BBB"));
        assertNull(simpleSetNoComparator.higher("aaa"));
        assertEquals("AAA", simpleSetNoComparator.higher("A"));
    }

    @Test
    void ceiling() {
        assertEquals("BBB", simpleSetNoComparator.ceiling("BBB"));
        assertNull(simpleSetNoComparator.ceiling("zzz"));
        assertEquals("AAA", simpleSetNoComparator.ceiling("A"));
    }
}