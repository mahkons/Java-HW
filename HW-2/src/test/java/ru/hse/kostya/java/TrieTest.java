package ru.hse.kostya.java;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {

    private Trie trie = new Trie();
    private Trie emptyTrie = new Trie();

    @BeforeEach
    void setUp() {
        trie.add("AAAB");
        trie.add("Aa");
    }

    @Test
    void size() {
        assertEquals(0, emptyTrie.size());
        assertEquals(2, trie.size());
        trie.remove("AAAB");
        assertEquals(1, trie.size());
    }

    @Test
    void add() {
        assertFalse(trie.add(null));
        assertFalse(trie.add("AAAB"));
        assertTrue(trie.add(""));
        assertTrue(trie.add("\u0001\u1000"));
        assertTrue(trie.contains("\u0001\u1000"));
    }

    @Test
    void contains() {
        assertFalse(trie.contains(null));
        assertTrue(trie.contains("Aa"));
        assertFalse(emptyTrie.contains("Aa"));
    }

    @Test
    void remove() {
        assertFalse(trie.remove(null));
        assertTrue(trie.remove("Aa"));
        assertFalse(trie.remove("Aa"));
        assertFalse(emptyTrie.remove("Aa"));
    }

    @Test
    void howManyStartsWithPrefix() {
        assertEquals(0, trie.howManyStartsWithPrefix(null));
        assertEquals(0, emptyTrie.howManyStartsWithPrefix(""));
        assertEquals(2, trie.howManyStartsWithPrefix("A"));
        trie.add("AAb");
        assertEquals(2, trie.howManyStartsWithPrefix("AA"));
    }

    @Test
    void serializeAndDeserialize() {
        //decided to check them together
        //due to difficulties of changing tests in case of
        //      serialisation algorithm modification
        var byteArrayOutputStream = new ByteArrayOutputStream(1000);
        try {
            trie.serialize(byteArrayOutputStream);
            trie.remove("Aa");

            trie.deserialize(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            assertEquals(2, trie.size());
            assertTrue(trie.contains("AAAB"));
            assertTrue(trie.contains("Aa"));

        } catch (IOException io) {
            // shouldn't get here. ByteArrayInputStream doesn't depend on any external resources
            // so it is programmer's error
            assert(false);
        }

    }

}