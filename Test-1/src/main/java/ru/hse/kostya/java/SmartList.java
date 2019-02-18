package ru.hse.kostya.java;

import org.jetbrains.annotations.NotNull;

import javax.management.MXBean;
import java.util.*;

/**
 * List Implementation that works faster with small Lists.
 */
public class SmartList<E> extends AbstractList<E> implements List<E> {

    private int size;
    private Object data;

    private static final int MAX_LOAD = 5;

    @Override
    public int size() {
        return size;
    }

    /**
     * Iterator for List.
     * Removed uses SmartList remove
     */
    private class SmartListIterator implements Iterator<E> {
        private int current;

        private SmartListIterator() { }

        @Override
        public boolean hasNext() {
            return current != size();
        }

        @Override
        public E next() {
            final E savedElement = get(current);
            current++;
            return savedElement;
        }

        @Override
        public void remove() {
            current--;
            SmartList.this.remove(current);
        }
    }

    public SmartList() {}

    public SmartList(Collection<? extends E> collection) {
        addAll(collection);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new SmartListIterator();
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index + " should be in range [0," + size + ")");
        }
        if (size <= 1) {
            return (E)data;
        }
        if (size <= MAX_LOAD) {
            var arrayOfE = (E[])data;
            return arrayOfE[index];
        }
        var arrayList = (ArrayList<E>)data;
        return arrayList.get(index);
    }

    /**
     * After adding an element changes type of data if needed
     */
    @SuppressWarnings("unchecked")
    private void grow() {
        if (size - 1 == 1) {
           var newArray = new Object[MAX_LOAD];
           newArray[0] = data;
           data = newArray;
        }
        if(size - 1 == MAX_LOAD) {
            var oldArray = (E[])data;
            data = new ArrayList<E>();
            for (int i = 0; i < size - 1; i++) {
                ((ArrayList) data).add(oldArray[i]);
            }
        }

    }

    /**
     * After removing an element changes type of data if needed
     */
    private void getSmaller() {
        if (size == 1) {
            @SuppressWarnings("unchecked")
            var arrayOfE = (E[])data;
            data = arrayOfE[0];
        }
        if (size == MAX_LOAD) {
            @SuppressWarnings("unchecked")
            var arrayList = (ArrayList<E>)data;
            var newArray = new Object[MAX_LOAD];
            for (int index = 0; index < size(); index++) {
                newArray[index] = arrayList.get(index);
            }
            data = newArray;
        }
    }

    @Override
    public boolean add(E element) {
        if (size == 0) {
            data = element;
        }
        size++;
        grow();
        if (size > MAX_LOAD) {
            @SuppressWarnings("unchecked")
            var arrayList = (ArrayList<E>)data;
            arrayList.add(element);
        }
        if(1 < size && size <= MAX_LOAD) {
            set(size - 1, element);
        }
        return true;
    }

    @Override
    public E set(int index, E element) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index + " should be in range [0," + size + ")");
        }
        final E savedElement = get(index);
        if (size == 1) {
            data = element;
        }
        if (1 < size && size <= MAX_LOAD) {
            @SuppressWarnings("unchecked")
            var arrayOfE = (E[])data;
            arrayOfE[index]= element;
        }
        if (size > MAX_LOAD) {
            @SuppressWarnings("unchecked")
            var arrayList = (ArrayList<E>)data;
            arrayList.set(index, element);
        }

        return savedElement;
    }

    @Override
    public E remove(int index) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index + " should be in range [0," + size + ")");
        }
        final E savedElement = get(index);
        if (size == 1) {
            data = null;
        }
        if (1 < size && size <= MAX_LOAD) {
            @SuppressWarnings("unchecked")
            var arrayOfE = (E[])data;
            for (int i = index; i + 1 < size; i++) {
                arrayOfE[i] = arrayOfE[i + 1];
            }
            arrayOfE[size - 1] = null;
        }
        if (size > MAX_LOAD) {
            @SuppressWarnings("unchecked")
            var arrayList = (ArrayList<E>)data;
            arrayList.remove(index);
        }
        size--;
        getSmaller();
        return savedElement;
    }



}