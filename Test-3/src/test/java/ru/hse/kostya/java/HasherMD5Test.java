package ru.hse.kostya.java;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class HasherMD5Test {

    private void checkEmptyDirectory(Path root, boolean multithreaded) throws IOException, NoSuchAlgorithmException {
        final String emptyDirName = "emptyDir";
        Path emptyDir = root.resolve(emptyDirName);
        Files.createDirectory(emptyDir);
        final byte[] hash;
        if (multithreaded) {
            hash = HasherMD5.hashMultithreaded(emptyDir);
        } else {
            hash = HasherMD5.hash(emptyDir);
        }
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(emptyDir.getFileName().toString().getBytes());

        assertArrayEquals(messageDigest.digest(), hash);
    }

    private void checkSimpleHierarchy(Path root, boolean multithreaded) throws IOException, NoSuchAlgorithmException {
        Path simpleDir = root.resolve("dir1");
        Files.createDirectory(simpleDir);
        Path dir2 = root.resolve("dir2");
        Files.createDirectory(dir2);
        Path dir3 = dir2.resolve("dir3");
        Files.createDirectory(dir3);

        Path file = dir2.resolve("file");
        Files.createFile(file);
        try (var output = Files.newOutputStream(file)) {
            output.write("Simple text".getBytes());
        }

        final byte[] hash;
        if (multithreaded) {
            hash = HasherMD5.hashMultithreaded(simpleDir);
        } else {
            hash = HasherMD5.hash(simpleDir);
        }
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        messageDigest.update(simpleDir.getFileName().toString().getBytes());

        assertArrayEquals(messageDigest.digest(), hash);

    }

    @Test
    void testEmptySingle(@TempDir Path root) throws IOException, NoSuchAlgorithmException {
        checkEmptyDirectory(root,false);
    }

    @Test
    void testEmptyMulti(@TempDir Path root) throws IOException, NoSuchAlgorithmException {
        checkEmptyDirectory(root,true);
    }

    @Test
    void testManySingle(@TempDir Path root) throws IOException, NoSuchAlgorithmException {
        checkSimpleHierarchy(root,false);
    }

    @Test
    void testManyMulti(@TempDir Path root) throws IOException, NoSuchAlgorithmException {
        checkSimpleHierarchy(root,true);
    }

}