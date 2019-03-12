package ru.hse.java.kostya;

import org.apache.xerces.dom.RangeImpl;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class Sorter {

    private static Comparator<?> comparator;
    private static final byte[] DEFAULT_SEED = "179".toString().getBytes();
    private static SecureRandom random = new SecureRandom(DEFAULT_SEED);
    private static final int LOWER_BOUND_FOR_INSERTION_SORT = 40;

    public static void setSeed(long seed) {
        random.setSeed(seed);
    }

    public static <T extends Comparable<? super T>, L extends List<T> & RandomAccess> void sort(L list) {
        sort(list, Comparator.naturalOrder());
        return;
    }

    public static <T, L extends List<T> & RandomAccess> void sort(L list, Comparator<? super T> comparatorParameter) {
        comparator = comparatorParameter;
        var pool = new ForkJoinPool();
        pool.invoke(new QuickSort<>(0, list.size(), list));
        return;
    }

    private static class QuickSort<T> extends RecursiveAction {
        private final int leftBound;
        private final int rightBound;
        List<T> data;

        private QuickSort(int leftBound, int rightBound, List<T> data) {
            this.leftBound = leftBound;
            this.rightBound = rightBound;
            this.data = data;
        }

        @Override
        public void compute() {
            if (rightBound - leftBound < LOWER_BOUND_FOR_INSERTION_SORT) {
                insertionSort();
                return;
            }
            int pivot = leftBound + random.nextInt(rightBound - leftBound);
            pivot = partition(pivot);
            invokeAll(new QuickSort<T>(leftBound, pivot, data),
                    new QuickSort<T>(pivot + 1, rightBound, data));
        }

        private int partition(int pivotIndex) {
            T pivotValue = data.get(pivotIndex);
            Collections.swap(data, pivotIndex, rightBound - 1);
            int storeIndex = leftBound;
            for(int i = leftBound; i < rightBound; i++) {
                if(isSmaller(data.get(i), pivotValue)) {
                    Collections.swap(data, storeIndex, i);
                    storeIndex++;
                }
            }
            Collections.swap(data, rightBound - 1, storeIndex);
            return storeIndex;
        }

        private void insertionSort() {
            for (int i = leftBound; i < rightBound; i++) {
                for (int j = i; j > leftBound && isSmaller(data.get(j), data.get(j - 1)); j--) {
                    Collections.swap(data, j - 1, j);
                }
            }
        }

        private boolean isSmaller(T first, T second) {
            @SuppressWarnings("unchecked")
            final boolean result = ((Comparator<? super T>)comparator).compare(first, second) < 0;
            return result;
        }
    }

}
