package ru.hse.kostya.java;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleMyTreeSetTest {

    private SimpleMyTreeSet<Object> emptyTreeSet = new SimpleMyTreeSet<>();
    private SimpleMyTreeSet<String> simpleSetNoComparator = new SimpleMyTreeSet<>();
    private SimpleMyTreeSet<String> simpleSetWithComparator = new SimpleMyTreeSet<>(String.CASE_INSENSITIVE_ORDER);

    @BeforeEach
    private void setUp() {
        simpleSetNoComparator.add("aaa");
        simpleSetNoComparator.add("AAA");
        simpleSetNoComparator.add("BBB");

        simpleSetWithComparator.add("aaa");
        simpleSetWithComparator.add("AAA");
        simpleSetWithComparator.add("BBB");
    }

    @Test
    private void size() {
        assertEquals(0, emptyTreeSet.size());
        assertEquals(3, simpleSetNoComparator.size());
    }

    @Test
    private void iterator() {
    }

    @Test
    private void descendingIterator() {
    }

    @Test
    private void descendingSet() {
    }

    @Test
    private void contains() {
        assertFalse(emptyTreeSet.contains("aaa"));
        assertFalse(simpleSetNoComparator.contains("aaA"));
        assertTrue(simpleSetNoComparator.contains("AAA"));
    }

    @Test
    private void add() {
        assertTrue(simpleSetNoComparator.add("bbb"));
        assertEquals("bbb", simpleSetNoComparator.last());
        assertFalse(simpleSetNoComparator.add("aaa"));
    }

    @Test
    private void remove() {
        assertTrue(simpleSetNoComparator.remove("aaa"));
        assertEquals("BBB", simpleSetNoComparator.last());
        assertFalse(simpleSetNoComparator.remove("bbb"));
    }

    @Test
    private void first() {
        assertEquals("AAA", simpleSetNoComparator.first());
        simpleSetNoComparator.remove("AAA");
        assertEquals("BBB", simpleSetNoComparator.first());
    }

    @Test
    private void last() {
        assertEquals("aaa", simpleSetNoComparator.last());
        simpleSetNoComparator.remove("aaa");
        assertEquals("BBB", simpleSetNoComparator.last());
    }

    @Test
    private void lower() {
        assertEquals("AAA", simpleSetNoComparator.lower("BBB"));
        assertNull(simpleSetNoComparator.lower("AAA"));
        assertEquals("aaa", simpleSetNoComparator.lower("zzz"));
    }

    @Test
    private void floor() {
        assertEquals("BBB", simpleSetNoComparator.floor("BBB"));
        assertNull(simpleSetNoComparator.floor("A"));
        assertEquals("aaa", simpleSetNoComparator.floor("zzz"));
    }

    @Test
    private void higher() {
        assertEquals("aaa", simpleSetNoComparator.higher("BBB"));
        assertNull(simpleSetNoComparator.higher("aaa"));
        assertEquals("AAA", simpleSetNoComparator.higher("A"));
    }

    @Test
    private void ceiling() {
        assertEquals("BBB", simpleSetNoComparator.ceiling("BBB"));
        assertNull(simpleSetNoComparator.ceiling("zzz"));
        assertEquals("AAA", simpleSetNoComparator.ceiling("A"));
    }
}