package ru.hse.kostya.java;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;


/**
 * MyTreeSet implementation using unbalanced search tree.
 * Compares elements by comparator, given in constructor or
 *      if there was no such comparator uses Comparable interface
 */
public class SimpleMyTreeSet<E> extends AbstractSet<E> implements MyTreeSet<E> {


    @Nullable private final Comparator<? super E> comparator;
    private final boolean isReversed;

    private int size = 0;
    private int modCount = 0;
    @Nullable private Node root;

    /**
     * Node of binary tree.
     * Comparing to current element, every node in left subtree contains smaller elements,
     *  in right subtree greater elements
     */
    private class Node {

        @NotNull private E content;
        @Nullable private Node left;
        @Nullable private Node right;
        @Nullable private Node parent;

        private Node(@NotNull E content) {
            this.content = content;
        }

        private Node(@NotNull E content, @NotNull Node parent) {
            this.content = content;
            this.parent = parent;
        }

        /**
         * Adds element to node subtree.
         * It is supposed, that there is no equal element
         */
        private void add(@NotNull E element) {
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


        /**
         * Removes element from node subtree.
         * It is supposed, that there is an equal element in subtree
         */
        @SuppressWarnings("ConstantConditions")
        //and so left and right cannot be null when requested
        private void remove(@NotNull Object element) {
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

        /**
         * Finds Node, containing least element bigger than one in current Node.
         * Returns null if there is no such
         */
        @Nullable private Node next() {
            if (right != null) {
                Node current = right;
                while (current.left != null) {
                    current = current.left;
                }
                return current;
            }
            Node current = this;
            while (current.parent != null && !current.isLeftSon()) {
                current = current.parent;
            }
            return current.parent;
        }

        /**
         * Finds Node, containing greatest element smaller than one in current Node.
         * Returns null if there is no such
         */
        @Nullable private Node previous() {
            if (left != null) {
                Node current = left;
                while (current.right != null) {
                    current = current.right;
                }
                return current;
            }
            Node current = this;
            while (current.parent != null && !current.isRightSon()) {
                current = current.parent;
            }
            return current.parent;
        }

        /**
         * Finds Node, containing greatest element smaller than given element.
         * Returns null if there is no such
         */
        @Nullable private Node lowerNode(@NotNull E element) {
            if (compareValues(element, content) > 0) {
                if (right != null) {
                    return right.lowerNode(element);
                } else {
                    return this;
                }
            } else {
                if (left != null) {
                    return left.lowerNode(element);
                } else {
                    Node current = this;
                    while (current.parent != null && current.isLeftSon()) {
                        current = current.parent;
                    }
                    return current.parent;
                }
            }
        }

        /**
         * Finds Node, containing least element bigger than given element.
         * Returns null if there is no such
         */
        @Nullable private Node upperNode(@NotNull E element) {
            if (compareValues(element, content) < 0) {
                if (left != null) {
                    return left.upperNode(element);
                } else {
                    return this;
                }
            } else {
                if (right != null) {
                    return right.upperNode(element);
                } else {
                    Node current = this;
                    while (current.parent != null && current.isRightSon()) {
                        current = current.parent;
                    }
                    return current.parent;
                }
            }
        }

        /**
         * Finds Node with the least value in subtree.
         */
        @NotNull private Node first() {
            if (left == null) {
                return this;
            }
            return left.first();
        }

        /**
         * Finds Node with the greatest value in subtree.
         */
        @NotNull private Node last() {
            if (right == null) {
                return this;
            }
            return right.last();
        }
    }

    /**
     * Constructor with no comparator.
     * Compares using Comparable interface
     * Initially tree is empty
     */
    public SimpleMyTreeSet() {
        isReversed = false;
        comparator = null;
    }


    /**
     * Constructor with give comparator.
     * Initially tree is empty
     */
    public SimpleMyTreeSet(@NotNull Comparator<? super E> comparator) {
        isReversed = false;
        this.comparator = comparator;
    }

    private SimpleMyTreeSet(@Nullable Comparator<? super E> comparator, boolean isReversed, int size, int modCount, @Nullable Node root) {
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


    /**
     * Iterator for SimpleTreeSet.
     * Iterator can be in reversed order as well.
     * Iterator throws ConcurrentModificationException if used after modifying tree
     */
    private class TreeSetIterator implements Iterator<E> {
        @Nullable private Node nextElement;
        private final boolean isReversed;
        private final int expectedModCount;

        private TreeSetIterator(@Nullable Node nextElement, boolean isReversed) {
            this.isReversed = isReversed;
            this.nextElement = nextElement;
            expectedModCount = modCount;
        }

        /**
         * Checks whether tree was modified since iterator construction
         */
        private boolean isNotValid() {
            return expectedModCount != modCount;
        }

        @Override
        public boolean hasNext() {
            if (isNotValid()) {
                throw new ConcurrentModificationException("Tree was modified");
            }
            return nextElement != null;
        }

        /**
         * Gets next element and moves iterator.
         */
        @Override
        @NotNull public E next() {
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

    /**
     * Creates iterator, that starts from the smallest element in the tree.
     */
    @Override
    @NotNull public Iterator<E> iterator() {
        Node startPoint = null;
        if (root != null) {
            startPoint = isReversed ? root.last() : root.first();
        }
        return new TreeSetIterator(startPoint, isReversed);
    }


    /**
     * Creates iterator, that starts from the biggest element in the tree and
     *  moves in direction of smallest one
     */
    @Override
    @NotNull public Iterator<E> descendingIterator() {
        Node startPoint = null;
        if (root != null) {
            startPoint = isReversed ? root.first() : root.last();
        }
        return new TreeSetIterator(startPoint, !isReversed);
    }


    /**
     * Returns a reverse order view of the elements contained in this set.
     * The descending set is backed by this set, so changes to the set are reflected
     *      in the descending set, and vice-versa.
     */
    @Override
    @NotNull public MyTreeSet<E> descendingSet() {
        return new SimpleMyTreeSet<>(comparator, !isReversed, size, modCount + 1, root);
    }

    /**
     * Checks whether set contains given element
     */
    //faster than contains from AbstractSet
    @Override
    public boolean contains(@NotNull Object element) {
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

    /**
     * Adds given element in set if it have not been in already
     */
    @Override
    public boolean add(@NotNull E element) {
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

    /**
     * Removes element from set if it was in set.
     */
    @SuppressWarnings("ConstantConditions")
    //if tree contains an element, root cannot be null
    @Override
    public boolean remove(@NotNull Object element) {
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

    /**
     * Returns smallest element in set
     *      or null if it is empty
     */
    @Override
    @Nullable public E first() {
        if (root == null) {
            return null;
        }

        return root.first().content;
    }

    /**
     * Returns greatest element in set
     *      or null if it is empty
     */
    @Override
    @Nullable public E last() {
        if (root == null) {
            return null;
        }

        return root.last().content;
    }

    /** {@link MyTreeSet#lower(E)}  */
    @Override
    @Nullable public E lower(@NotNull E element) {
        if (root == null) {
            return null;
        }
        Node lowerNode = root.lowerNode(element);
        if (lowerNode == null) {
           return null;
        }
        return lowerNode.content;
    }

    /** {@link MyTreeSet#floor(E)}  */
    @Override
    @Nullable public E floor(@NotNull E element) {
        if (contains(element)) {
            return element;
        }
        return lower(element);
    }

    /** {@link MyTreeSet#higher(E)}  */
    @Override
    @Nullable public E higher(@NotNull E element) {
        if (root == null) {
            return null;
        }
        Node upperNode = root.upperNode(element);
        if (upperNode == null) {
            return null;
        }
        return upperNode.content;
    }

    /** {@link MyTreeSet#ceiling(E)}  */
    @Override
    @Nullable public E ceiling(@NotNull E element) {
        if (contains(element)) {
            return element;
        }
        return higher(element);
    }

    /**
     * Compare given values using comparator ot
     *      if there is no comparator, comparable interface.
     * @throws ClassCastException if none of those abilities was given
     */
    @SuppressWarnings("unchecked")
    private int compareValues(@NotNull Object a, @NotNull Object b) {
        if (comparator != null) {
            return comparator.compare((E)a, (E)b);
        }

        return ((Comparable<? super E>)a).compareTo((E)b);
    }

    /**
     * Merges two trees.
     * NB! all elements in a ought to be strictly smaller than elements in b
     */
    @Nullable private Node merge(@Nullable Node a, @Nullable Node b) {
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
