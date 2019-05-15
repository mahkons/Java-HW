package ru.hse.kostya.java;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class HasherMD5 {

    private static final int BUFFER_SIZE = 4096;

    public static byte[] hash(Path path) throws NoSuchAlgorithmException, IOException {
        if (Files.isDirectory(path)) {
            return directoryHash(path);
        }
        if (Files.isRegularFile(path)) {
            return fileHash(path);
        }
        //ignore
        return new byte[0];
    }

    public static byte[] hashMultithreaded(Path path) {
        return (new ForkJoinPool().invoke(new HasherTask(path)));
    }

    private static byte[] directoryHash(Path path) throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        messageDigest.update(path.getFileName().toString().getBytes());
        for (Path filePath : Files.walk(path).collect(Collectors.toList())) {
            messageDigest.update(hash(filePath));
        }
        return messageDigest.digest();
    }

    private static byte[] fileHash(Path path) throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        try (InputStream input = Files.newInputStream(path)) {
            var digestStream = new DigestInputStream(input, messageDigest);
            var buffer = new byte[BUFFER_SIZE];

            while (digestStream.read(buffer) != -1) { }
        }
        return messageDigest.digest();
    }

    private static class HasherTask extends RecursiveTask<byte[]> {

        private final Path path;

        private HasherTask(Path path) {
            this.path = path;
        }

        @Override
        protected byte[] compute() {
            try {
                if (Files.isDirectory(path)) {
                    return computeDirectoryHash();
                }
                if (Files.isRegularFile(path)) {
                    return computeFileHash();
                }
            } catch (Exception exception) {
                //AAAA panic
                System.exit(1);
            }
            //ignore
            return new byte[0];
        }

        private byte[] computeDirectoryHash() throws NoSuchAlgorithmException, IOException,
                ExecutionException, InterruptedException {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.update(path.getFileName().toString().getBytes());

            for (HasherTask hash : invokeAll(Files.walk(path).map(HasherTask::new).collect(Collectors.toList()))) {
                messageDigest.update(hash.get());
            }

            return messageDigest.digest();
        }

        private byte[] computeFileHash() throws IOException, NoSuchAlgorithmException {
            return fileHash(path);
        }
    }

}
