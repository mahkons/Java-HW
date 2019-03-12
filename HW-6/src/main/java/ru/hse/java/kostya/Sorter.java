package ru.hse.java.kostya;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Class that sorts given List of elements according to there naturalOrder or given comparator.
 * Uses quickSort as algorithm for sorting
 * Accepts List with RandomAccess order as argument
 * Implementation is Multithreaded
 */
public class Sorter {

    private static Comparator<?> comparator;
    private static final byte[] DEFAULT_SEED = "179".getBytes();
    private static SecureRandom random = new SecureRandom(DEFAULT_SEED);
    private static final int LOWER_BOUND_FOR_INSERTION_SORT = 40;

    /**
     * Allows changing seed for quickSort algorithm.
     */
    public static void setSeed(long seed) {
        random.setSeed(seed);
    }

    /**
     * Sorts given list according to natural order of its elements.
     */
    public static <T extends Comparable<? super T>, L extends List<T> & RandomAccess> void sort(L list) {
        sort(list, Comparator.naturalOrder());
        return;
    }

    /**
     * Sorts list according to provided comparator.
     */
    public static <T, L extends List<T> & RandomAccess> void sort(L list, Comparator<? super T> comparatorParameter) {
        comparator = comparatorParameter;
        var pool = new ForkJoinPool();
        pool.invoke(new QuickSort<>(0, list.size(), list));
        return;
    }

    /**
     *  RecursiveAction class for ForkJoinPool that does all the work.
     */
    private static class QuickSort<T> extends RecursiveAction {
        private final int leftBound;
        private final int rightBound;
        List<T> data;

        private QuickSort(int leftBound, int rightBound, List<T> data) {
            this.leftBound = leftBound;
            this.rightBound = rightBound;
            this.data = data;
        }

        /**
         * If array is small uses InsertionSort,
         *  otherwise does partition, splits array in two parts and puts new sort tasks to pool.
         */
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
            for (int i = leftBound; i < rightBound; i++) {
                if (isSmaller(data.get(i), pivotValue)) {
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

        /**
         * Compares elements according to comparator.
         */
        private boolean isSmaller(T first, T second) {
            @SuppressWarnings("unchecked")
            //Cast would succeed cause Comparator obtained in sort method satisfies condition
            final boolean result = ((Comparator<? super T>)comparator).compare(first, second) < 0;
            return result;
        }
    }

}
