package ru.hse.java.kostya;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SorterTest {

    private <T> boolean isSorted(List<T> list, Comparator<? super T> comparator) {
        for (int i = 0; i + 1 < list.size(); i++) {
            if (comparator.compare(list.get(i), list.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }

    @Test
    void simpleSortOfIntegers() {
        var random = new Random(179);
        ArrayList<Integer> list = random.ints(100*1000).boxed().collect(Collectors.toCollection(ArrayList::new));
        Sorter.sort(list);
        assertTrue(isSorted(list, Comparator.naturalOrder()));
    }

    @Test
    void simpleTestWithComparator() {
        var random = new Random(179);
        ArrayList<Integer> list = random.ints(100*1000).boxed().collect(Collectors.toCollection(ArrayList::new));
        Sorter.sort(list, Comparator.reverseOrder());
        assertTrue(isSorted(list, Comparator.reverseOrder()));
    }

}