package ru.hse.kostya.java.hashtable;

/**
 * Data structure. Allows adding, removing and modifying element in linear time
 * Implemented as LinkedList
 */
public class List {

    private Node head;
    private int size;

    /**
     * Contains some information and links to
     *      previous and next Nodes.
     * Or null if they do not exist
     */
    private static class Node {

        private PairStringString pairStringString;
        private Node next;
        private Node prev;

        private Node(PairStringString pairStringString, Node next, Node prev) {
            this.pairStringString = pairStringString;
            this.next = next;
            this.prev = prev;
        }

        /**
         * Changes neighbour's links, leads to removing Node from List.
         */
        private void remove() {
            if (prev != null) {
                prev.next = next;
            }
            if (next != null) {
                next.prev = prev;
            }
        }
    }


    public int size() {
        return size;
    }

    /**
     * Checks whether there are no elements in List.
     */
    public boolean empty() {
        return size == 0;
    }

    /**
     * Finds Node with same key as given.
     * It is guaranteed(due to features of adding function) to be only one such Node
     */
    private Node find(String key) {
        Node now = head;
        while (now != null) {
            if (now.pairStringString.getKey().equals(key)) {
                return now;
            }
            now = now.next;
        }
        return null;
    }

    /**
     * Checks whether given key appears in List.
     */
    public boolean contains(String key) {
        return find(key) != null;
    }

    /**
     * Returns values stored with given key, if any.
     * Null otherwise
     */
    public String get(String key) {
        Node pos = find(key);
        if (pos == null) {
            return null;
        } else {
            return pos.pairStringString.getValue();
        }
    }

    /**
     * Modifies element with same key, if any.
     * Adds new element otherwise
     */
    public String put(String key, String value) {
        PairStringString p = new PairStringString(key, value);
        Node pos = find(key);
        if (pos == null) {
            head = new Node(p, head, null);
            if (head.next != null) {
                head.next.prev = head;
            }
            size++;
            return null;
        } else {
            PairStringString tmp = pos.pairStringString;
            pos.pairStringString = p;
            return tmp.getValue();
        }
    }

    /**
     * Remove element with same key, if any.
     */
    public String remove(String key) {
        Node pos = find(key);
        if (pos == null) {
            return null;
        }
        size--;

        if (head == pos) {
            head = pos.next;
        }
        pos.remove();

        return pos.pairStringString.getValue();
    }

    /**
     * Gets head element from List and removes it.
     * return null if List is empty
     */
    public PairStringString popHeadElement() {
        if (head == null) {
            return null;
        }
        final PairStringString elem = head.pairStringString;
        head.remove();
        head = head.next;
        size--;
        return elem;
    }


    /**
     * Deletes all content.
     */
    public void clear() {
        head = null;
        size = 0;
    }

}
