package ru.hse.kostya.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Main {

    private static byte[] runManyThreads(Path path) {
        long timeNanoStart = System.nanoTime();
        final byte[] hash = HasherMD5.hashMultithreaded(path);
        long timeNanoEnd = System.nanoTime();
        System.out.println("Multithreaded version worked for " + (timeNanoEnd - timeNanoStart) * 1e-9 + " seconds.");
        return hash;
    }

    private static byte[] runSingleThread(Path path) throws IOException, NoSuchAlgorithmException {
        long timeNanoStart = System.nanoTime();
        final byte[] hash = HasherMD5.hash(path);
        long timeNanoEnd = System.nanoTime();
        System.out.println("Singlethread version worked for " + (timeNanoEnd - timeNanoStart) * 1e-9 + " seconds.");
        return hash;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        if (args.length < 1) {
            throw new IllegalArgumentException("you should specify path as first argument");
        }
        byte[] singleHash = runSingleThread(Paths.get(args[0]));
        byte[] multiHash = runManyThreads(Paths.get(args[0]));

        if (!Arrays.equals(singleHash, multiHash)) {
            throw new AssertionError("versions with single thread and few threads returned different results");
        }

    }

}
