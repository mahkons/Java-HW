package ru.hse.kostya.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class Trie implements ISerializable {

    private class TrieNode {
        private int howManyStartsInNode;
        private HashMap<Character, TrieNode> tableOfChildren =
                new HashMap<Character, TrieNode>();
        private boolean someStringEndsHere;

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

    }


    private int size;
    private TrieNode root = new TrieNode();

    public int size() {
        return size;
    }

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
        return true;
    }

    public boolean contains(String element) {
        TrieNode trieNode = simplifiedGoToNode(element);
        return trieNode != null && trieNode.someStringEndsHere;
    }

    public boolean remove (String element) {
        if (element == null || !contains(element)) {
            return false;
        }
        TrieNode positionNow = root;
        root.howManyStartsInNode--;
        for (char ch : element.toCharArray()) {
            if (positionNow.next(ch) == null) {
                throw new AssertionError(); //can't happen, Trie contains element
            }
            if(positionNow.next(ch).howManyStartsInNode == 1) {
                //branch of trie should be deleted
                positionNow.removeChild(ch);
                break;
            }
            positionNow = positionNow.next(ch);
            positionNow.howManyStartsInNode--;
        }
        positionNow.someStringEndsHere = false;
        return true;
    }

    int howManyStartsWithPrefix(String prefix) {
        if (prefix == null) {
            return 0;
        }
        TrieNode trieNode = simplifiedGoToNode(prefix);
        return trieNode == null ? 0 : trieNode.howManyStartsInNode;
    }


    @Override
    public void serialize(OutputStream out) throws IOException {

    }

    @Override
    public void deserialize(InputStream in) throws IOException {

    }
}
