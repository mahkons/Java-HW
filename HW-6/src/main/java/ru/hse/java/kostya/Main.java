package ru.hse.java.kostya;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {

    private static ArrayList<Integer> generateIntegerList(int length) {
        var random = new Random(179);
        ArrayList<Integer> list = random.ints(length).boxed().collect(Collectors.toCollection(ArrayList::new));
        return list;
    }

    private static long runMultiThreadedSort(int length) {
        ArrayList<Integer> list = generateIntegerList(length);
        long startTime = System.nanoTime();
        Sorter.sort(list);
        long stopTime = System.nanoTime();
        return stopTime - startTime;
    }

    private static long runSingleThreadSort(int length) {
        ArrayList<Integer> list = generateIntegerList(length);
        long startTime = System.nanoTime();
        SortInCurrentThread.sort(list);
        long stopTime = System.nanoTime();
        return stopTime - startTime;
    }

    private static long runLibrarySort(int length) {
        ArrayList<Integer> list = generateIntegerList(length);
        Integer[] array = list.toArray(new Integer[list.size()]);
        long startTime = System.nanoTime();
        Arrays.sort(array);
        long stopTime = System.nanoTime();
        return stopTime - startTime;
    }

    private static void compareSorts(int length) {
        System.out.println("SingleThread. Size: " + length + ". Time: " + runSingleThreadSort(length)/1000_000_000.0 + " seconds\n");
        System.out.println("MultiThread. Size: " + length + ". Time: " + runMultiThreadedSort(length)/1000_000_000.0 + " seconds\n");
        System.out.println("JavaSort. Size: " + length + ". Time: " + runLibrarySort(length)/1000_000_000.0 + " seconds\n");
        System.out.println("\n");
    }

    public static void main(String[] args) throws Exception {
        compareSorts(10_000);
        compareSorts(50_000);
        compareSorts(100_000);
        compareSorts(1000_000);
        compareSorts(10_000_000);
    }
}
