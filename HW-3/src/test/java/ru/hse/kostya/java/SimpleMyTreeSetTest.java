package ru.hse.kostya.java;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

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
        simpleSetWithComparator.add("ccc");
        simpleSetWithComparator.add("BBB");
    }

    @Test
    void size_addingElements() {
        assertEquals(3, simpleSetNoComparator.size());
        simpleSetNoComparator.add("CCC");
        assertEquals(4, simpleSetNoComparator.size());
        simpleSetNoComparator.add("CCC");
        assertEquals(4, simpleSetNoComparator.size());
    }

    @Test
    void size_removingElements() {
        assertEquals(3, simpleSetNoComparator.size());
        simpleSetNoComparator.remove("aaa");
        assertEquals(2, simpleSetNoComparator.size());
        simpleSetNoComparator.remove("aaa");
        assertEquals(2, simpleSetNoComparator.size());
    }

    @Test
    void iterator_GoesThroughWholeSet() {
        var summary = new StringBuilder();
        for (String elem : simpleSetNoComparator) {
            summary.append(elem);
        }
        assertEquals("AAABBBaaa", summary.toString());

        Iterator<String> setIteratorToEnd = simpleSetWithComparator.iterator();
        for (int i = 0; i < simpleSetWithComparator.size(); i++) {
            setIteratorToEnd.next();
        }
        assertFalse(setIteratorToEnd.hasNext());
    }

    @Test
    void iterator_ConcurrentModification() {
        Iterator<String> setIterator = simpleSetNoComparator.iterator();
        assertEquals("AAA", setIterator.next());

        assertThrows(ConcurrentModificationException.class, () -> {
            simpleSetNoComparator.add("ccc");
            assertTrue(setIterator.hasNext());
        });
    }

    @Test
    void iterator_NoNextForLastElement() {
        Iterator<String> setIterator = simpleSetNoComparator.iterator();
        for (int j = 0; j < simpleSetNoComparator.size(); j++) {
            setIterator.next();
        }
        assertThrows(NoSuchElementException.class, setIterator::next);

        Iterator<Object> emptySetIterator = emptyTreeSet.iterator();
        assertThrows(NoSuchElementException.class, emptySetIterator::next);
    }

    @Test
    void descendingIterator() {
        Iterator<String> setDescendingIterator = simpleSetNoComparator.descendingIterator();
        assertEquals("aaa", setDescendingIterator.next());
        assertEquals("BBB", setDescendingIterator.next());
        assertEquals("AAA", setDescendingIterator.next());
        assertFalse(setDescendingIterator.hasNext());
    }

    @Test
    void descendingSet_goingThroughWholeSet() {
        MyTreeSet<String> descendingTreeSet = simpleSetNoComparator.descendingSet();

        var summary = new StringBuilder();
        for (String elem : descendingTreeSet) {
            summary.append(elem);
        }
        assertEquals("aaaBBBAAA", summary.toString());

        assertEquals("aaa", descendingTreeSet.lower("BBB"));
        assertEquals("BBB", descendingTreeSet.ceiling("BBB"));
    }

    @Test
    void descendingSet_sharesTreeWithSetItMadeFrom() {
        MyTreeSet<String> descendingTreeSet = simpleSetNoComparator.descendingSet();
        assertFalse(descendingTreeSet.contains("ccc"));
        simpleSetNoComparator.add("ccc");
        assertTrue(descendingTreeSet.contains("ccc"));
        assertEquals(4, descendingTreeSet.size());
        descendingTreeSet.remove("aaa");
        assertFalse(simpleSetNoComparator.contains("aaa"));

    }

    @Test
    void contains_existing() {
        assertTrue(simpleSetWithComparator.contains("aaa"));
        assertTrue(simpleSetNoComparator.contains("aaa"));
    }

    @Test
    void contains_missing() {
        assertFalse(emptyTreeSet.contains("aaa"));
        assertFalse(simpleSetNoComparator.contains("aaA"));
    }

    @Test
    void add_existing() {
        assertFalse(simpleSetNoComparator.add("aaa"));
        assertFalse(simpleSetWithComparator.add("aaa"));
    }

    @Test
    void add_missing() {
        assertTrue(simpleSetNoComparator.add("bbb"));
        assertEquals("bbb", simpleSetNoComparator.last());
        assertFalse(simpleSetNoComparator.add("bbb"));
    }

    @Test
    void remove_existing() {
        assertTrue(simpleSetNoComparator.remove("aaa"));
        assertFalse(simpleSetNoComparator.contains("aaa"));
        assertEquals("BBB", simpleSetNoComparator.last());
        assertFalse(simpleSetNoComparator.remove("aaa"));
    }

    @Test
    void remove_missing() {
        assertFalse(simpleSetNoComparator.remove("ccc"));
        assertFalse(simpleSetWithComparator.remove("zzz"));
        assertEquals(3, simpleSetWithComparator.size());
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

        assertEquals("BBB", simpleSetWithComparator.lower("ccc"));
    }

    @Test
    void floor() {
        assertEquals("BBB", simpleSetNoComparator.floor("BBB"));
        assertNull(simpleSetNoComparator.floor("A"));
        assertEquals("aaa", simpleSetNoComparator.floor("zzz"));

        assertEquals("ccc", simpleSetWithComparator.floor("ccc"));
    }

    @Test
    void higher() {
        assertEquals("aaa", simpleSetNoComparator.higher("BBB"));
        assertNull(simpleSetNoComparator.higher("aaa"));
        assertEquals("AAA", simpleSetNoComparator.higher("A"));

        assertEquals("BBB", simpleSetWithComparator.higher("aaa"));
    }

    @Test
    void ceiling() {
        assertEquals("BBB", simpleSetNoComparator.ceiling("BBB"));
        assertNull(simpleSetNoComparator.ceiling("zzz"));
        assertEquals("AAA", simpleSetNoComparator.ceiling("A"));

        assertEquals("ccc", simpleSetWithComparator.ceiling("ccc"));
    }
}