package ru.hse.kostya.java;

import java.io.*;
import java.util.HashMap;

/**
 *Trie implemented as a rooted tree with symbols on edges.
 *Non-trivial methods works in linear from input time
 */
public class Trie implements ISerializable {

    private class TrieNode {
        private int howManyStartsInNode;
        private boolean someStringEndsHere;
        private HashMap<Character, TrieNode> tableOfChildren =
                new HashMap<Character, TrieNode>();

        private TrieNode() {}

        private TrieNode next(char c) {
            return tableOfChildren.get(c);
        }

        private void setChild(char c, TrieNode trieNode) {
            tableOfChildren.put(c, trieNode);
        }

        private void removeChild(char c) {
            tableOfChildren.remove(c);
        }

        //Trie Node doesn't implement ISerializable
        //Cause it is more comfortable to work with ObjectStreams
        //And it is private so it doesn't matter outside Trie class
        private void serialize(ObjectOutputStream objectOutStream) throws IOException {
            objectOutStream.writeInt(howManyStartsInNode);
            objectOutStream.writeBoolean(someStringEndsHere);
            objectOutStream.writeInt(tableOfChildren.size());
            for (var element : tableOfChildren.entrySet()) {
                objectOutStream.writeChar(element.getKey());
                element.getValue().serialize(objectOutStream);
            }
        }

        private void deserialize(ObjectInputStream objectInStream) throws IOException {
            howManyStartsInNode = objectInStream.readInt();
            someStringEndsHere = objectInStream.readBoolean();
            tableOfChildren.clear();
            int numberOfChildren = objectInStream.readInt();
            for (int i = 0; i < numberOfChildren; i++) {
                var trieNode = new TrieNode();
                char charOnEdge = objectInStream.readChar();
                tableOfChildren.put(charOnEdge, trieNode);
                trieNode.deserialize(objectInStream);
            }
        }
    }


    private int size;
    private TrieNode root = new TrieNode();

    public int size() {
        return size;
    }

    /**
     * Descends from root to node corresponding to prefix, without making new nodes.
     * @return desired node if descending is possible and null otherwise
     */
    private TrieNode simplifiedGoToNode(String prefix) {
        if (prefix == null) {
            return null;
        }
        TrieNode positionNow = root;
        for (char ch : prefix.toCharArray()) {
            if (positionNow.next(ch) == null) {
                return null;
            }
            positionNow = positionNow.next(ch);
        }
        return positionNow;
    }

    /**
     * Adds given string to Trie.
     * Descends from root to node, which corresponds to element
     * Makes new nodes if needed
     * @return false if element is null ot Trie contains it already
     */
    public boolean add(String element) {
        if (element == null || contains(element)) {
            return false;
        }
        TrieNode positionNow = root;
        root.howManyStartsInNode++;
        for (char ch : element.toCharArray()) {
            if (positionNow.next(ch) == null) {
                positionNow.setChild(ch, new TrieNode());
            }
            positionNow = positionNow.next(ch);
            positionNow.howManyStartsInNode++;
        }
        positionNow.someStringEndsHere = true;
        size++;
        return true;
    }

    /**
     * Checks whether given string contained in Trie.
     */
    public boolean contains(String element) {
        TrieNode trieNode = simplifiedGoToNode(element);
        return trieNode != null && trieNode.someStringEndsHere;
    }

    /**
     * Removes given string from Trie.
     * @return false if there is no such element in Trie or element is null and true otherwise
     * Deletes Nodes, which contains no strings
     */
    public boolean remove(String element) {
        if (element == null || !contains(element)) {
            return false;
        }
        TrieNode positionNow = root;
        root.howManyStartsInNode--;
        for (char ch : element.toCharArray()) {
            if (positionNow.next(ch) == null) {
                throw new AssertionError(); //can't happen, Trie contains element
            }
            if (positionNow.next(ch).howManyStartsInNode == 1) {
                //branch of trie should be deleted
                positionNow.removeChild(ch);
                break;
            }
            positionNow = positionNow.next(ch);
            positionNow.howManyStartsInNode--;
        }
        positionNow.someStringEndsHere = false;
        size--;
        return true;
    }

    /**
     * Counts number of strings in trie, started with prefix.
     * if prefix is null returns 0
     */
    int howManyStartsWithPrefix(String prefix) {
        TrieNode trieNode = simplifiedGoToNode(prefix);
        return trieNode == null ? 0 : trieNode.howManyStartsInNode;
    }


    /**
     * Serializes TrieNode.
     * Implemented by writing all valuable fields
     * Deserialization of internal tree implemented recursively
     * Details omitted as changeable
     */
    @Override
    public void serialize(OutputStream out) throws IOException {
        try (var objectOutStream = new ObjectOutputStream(out)) {
            objectOutStream.writeInt(size);
            root.serialize(objectOutStream);
        }
    }

    /**
     * Deserialize TrieNode.
     * Correlates with serialize implementation
     * Details omitted as changeable
     */
    @Override
    public void deserialize(InputStream in) throws IOException {
        try (var objectInStream = new ObjectInputStream(in)) {
            size = objectInStream.readInt();
            root.deserialize(objectInStream);
        }
    }
}
