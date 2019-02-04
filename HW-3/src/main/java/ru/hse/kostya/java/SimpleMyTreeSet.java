package ru.hse.kostya.java;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;


public class SimpleMyTreeSet<E> extends AbstractSet<E> implements MyTreeSet<E> {


    private final Comparator<? super E> comparator;
    private final boolean isReversed;

    private int size = 0;
    private int modCount = 0;
    private Node root;

    private class Node {

        private E content;
        private Node left;
        private Node right;
        private Node parent;

        private Node(E content) {
            this.content = content;
        }

        private Node(E content, Node parent) {
            this.content = content;
            this.parent = parent;
        }

        private void add(E element) {
            if (compareValues(content, element) > 0) {
                if (left == null) {
                    left = new Node(element, this);
                } else {
                    left.add(element);
                }

            } else {
                if (right == null) {
                    right = new Node(element, this);
                } else {
                    right.add(element);
                }
            }
        }

        private void remove(Object element) {
            if (compareValues(content, element) > 0) {
                if (left.equals(element)) {
                    left = merge(left.left, left.right);
                } else {
                    left.remove(element);
                }
            } else {
                if (right.equals(element)) {
                    right = merge(right.left, right.right);
                } else {
                    right.remove(element);
                }
            }
        }

        private boolean isLeftSon() {
            return parent != null && parent.left == this;
        }

        private boolean isRightSon() {
            return parent != null && parent.right == this;
        }

        private Node next() {
            if (right != null) {
                Node current = right;
                while (current.left != null) {
                    current = current.left;
                }
                return current;
            }
            Node current = this;
            while (!current.isLeftSon()) {
                current = current.parent;
            }
            return current.parent;
        }

        private Node previous() {
            if (left != null) {
                Node current = left;
                while (current.right != null) {
                    current = current.right;
                }
                return current;
            }
            Node current = this;
            while (!current.isRightSon()) {
                current = current.parent;
            }
            return current.parent;
        }

        private Node lowerNode(E element) {
            Node current = this;
            while (current != null) {
                if (compareValues(element, current.content) > 0) {
                    if (current.right != null) {
                        current = current.right;
                    } else {
                        return current;
                    }
                } else {
                    if (current.left != null) {
                        current = current.left;
                    } else {
                        while (current.isLeftSon()) {
                            current = current.parent;
                        }
                        return current.parent;
                    }
                }
            }
            return null;
        }
        private Node upperNode(E element) {
            Node current = this;
            while (current != null) {
                if (compareValues(element, current.content) < 0) {
                    if (current.left != null) {
                        current = current.left;
                    } else {
                        return current;
                    }
                } else {
                    if (current.right != null) {
                        current = current.right;
                    } else {
                        while (current.isRightSon()) {
                            current = current.parent;
                        }
                        return current.parent;
                    }
                }
            }
            return null;
        }

        private Node first() {
            if (left == null) {
                return this;
            }
            return left.first();
        }

        private Node last() {
            if (right == null) {
                return this;
            }
            return right.last();
        }
    }

    public SimpleMyTreeSet() {
        isReversed = false;
        comparator = null;
    }

    public SimpleMyTreeSet(Comparator<? super E> comparator) {
        isReversed = false;
        this.comparator = comparator;
    }

    private SimpleMyTreeSet(Comparator<? super E> comparator, boolean isReversed, int size, int modCount, Node root) {
        this.comparator = comparator;
        this.isReversed = isReversed;
        this.size = size;
        this.modCount = modCount;
        this.root = root;
    }

    @Override
    public int size() {
        return size;
    }


    private class TreeSetIterator implements Iterator<E> {
        private Node nextElement;
        private final boolean isReversed;
        private final int expectedModCound;

        private TreeSetIterator(Node nextElement, boolean isReversed) {
            this.isReversed = isReversed;
            this.nextElement = nextElement;
            expectedModCound = modCount;
        }

        private boolean isNotValid() {
            return expectedModCound != modCount;
        }

        @Override
        public boolean hasNext() {
            if (isNotValid()) {
                throw new ConcurrentModificationException("Tree was modified");
            }
            return nextElement != null;
        }

        @Override
        public E next() {
            if (isNotValid()) {
                throw new ConcurrentModificationException("Tree was modified");
            }
            if (nextElement == null) {
                throw new IllegalArgumentException("No next element");
            }
            final Node savedElement = nextElement;
            nextElement = isReversed ? nextElement.previous() : nextElement.next();
            return savedElement.content;
        }
    }

    @Override
    public Iterator<E> iterator() {
        Node startPoint = isReversed ? root.last() : root.first();
        return new TreeSetIterator(startPoint, isReversed);
    }


    @Override
    public Iterator<E> descendingIterator() {
        Node startPoint = isReversed ? root.first() : root.last();
        return new TreeSetIterator(startPoint, !isReversed);
    }

    @Override
    public MyTreeSet<E> descendingSet() {
        return new SimpleMyTreeSet<>(comparator, !isReversed, size, modCount + 1, root);
    }

    @Override
    public boolean contains(Object element) {
        if (root == null) {
            return false;
        }

        Node current = root;
        while (current != null) {
            if (current.content.equals(element)) {
                return true;
            }
            if (compareValues(element, current.content) > 0) {
                current = current.right;
            }
            else {
                current = current.left;
            }
        }
        return false;
    }

    @Override
    public boolean add(E element) {
        if (contains(element)) {
            return false;
        }
        modCount++;
        size++;

        if (root == null) {
            root = new Node(element);
            return true;
        }
        root.add(element);

        return true;
    }

    @Override
    public boolean remove(Object element) {
        if (!contains(element)) {
            return false;
        }
        modCount++;
        size--;

        if (root.equals(element)) {
            root = merge(root.left, root.right);
            return true;
        }
        root.remove(element);

        return true;
    }

    @Override
    public E first() {
        if (root == null) {
            return null;
        }

        return root.first().content;
    }

    @Override
    public E last() {
        if (root == null) {
            return null;
        }

        return root.last().content;
    }

    @Override
    public E lower(E element) {
        if (root == null) {
            return null;
        }
        Node lowerNode = root.lowerNode(element);
        if (lowerNode == null) {
           return null;
        }
        return lowerNode.content;
    }

    @Override
    public E floor(E element) {
        if (contains(element)) {
            return element;
        }
        return lower(element);
    }

    @Override
    public E higher(E element) {
        if (root == null) {
            return null;
        }
        Node upperNode = root.upperNode(element);
        if (upperNode == null) {
            return null;
        }
        return upperNode.content;
    }

    @Override
    public E ceiling(E element) {
        if (contains(element)) {
            return element;
        }
        return higher(element);
    }

    @SuppressWarnings("unchecked")
    private int compareValues(Object a, Object b) {
        if (comparator != null) {
            return comparator.compare((E)a, (E)b);
        }

        return ((Comparable<? super E>)a).compareTo((E)b);
    }

    private Node merge(Node a, Node b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }

        Node newMergedNode = merge(a, b.left);
        if (newMergedNode != null) {
            newMergedNode.parent = b;
        }
        return b.left = newMergedNode;
    }

}
