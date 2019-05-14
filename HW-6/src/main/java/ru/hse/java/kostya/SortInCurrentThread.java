package ru.hse.java.kostya;


import java.security.SecureRandom;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

/**
 * Class that sorts given List of elements according to there naturalOrder or given comparator.
 * Uses quickSort as algorithm for sorting
 * Accepts List with RandomAccess order as argument
 * Implementation is Singlethreaded
 */
public class SortInCurrentThread {

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
        compute(0, list.size(), list);
        return;
    }

    /**
     * If array is small uses InsertionSort,
     *  otherwise does partition, splits array in two parts and sorts them recursively.
     */
    private static <T> void compute(int leftBound, int rightBound, List<T> list) {
        if (rightBound - leftBound < LOWER_BOUND_FOR_INSERTION_SORT) {
            insertionSort(leftBound, rightBound, list);
            return;
        }
        int pivot = leftBound + random.nextInt(rightBound - leftBound);
        pivot = partition(leftBound, rightBound, list, pivot);
        compute(leftBound, pivot, list);
        compute(pivot + 1, rightBound, list);
    }

    private static <T> int partition(int left, int right, List<T> list, int pivotIndex) {
        T pivotValue = list.get(pivotIndex);
        Collections.swap(list, pivotIndex, right - 1);
        int storeIndex = left;
        for (int i = left; i < right; i++) {
            if (isBigger(pivotValue, list.get(i))) {
                Collections.swap(list, storeIndex, i);
                storeIndex++;
            }
        }
        Collections.swap(list, right - 1, storeIndex);
        return storeIndex;
    }

    private static <T> void insertionSort(int leftBound, int rightBound, List<T> list) {
        for (int i = leftBound; i < rightBound; i++) {
            for (int j = i; j > leftBound && isBigger(list.get(j - 1), list.get(j)); j--) {
                Collections.swap(list, j - 1, j);
            }
        }
    }

    /**
     * Compares elements according to comparator.
     */
    private static <T> boolean isBigger(T first, T second) {
        @SuppressWarnings("unchecked")
        final boolean result = ((Comparator<? super T>)comparator).compare(first, second) > 0;
        return result;
    }

}

