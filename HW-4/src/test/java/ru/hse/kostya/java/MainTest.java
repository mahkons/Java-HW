package ru.hse.kostya.java;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    //simple test to show interaction works correctly
    //test itself is in resources files {src/test/resources/MainTest_simpleInteraction}
    @Test
    void simpleInteraction() throws IOException {
        var inputFile = new File("src/test/resources/MainTest_simpleInteraction.in");
        var outputFile = new File("src/test/resources/MainTest_simpleInteraction.out");
        var ansFile = new File("src/test/resources/MainTest_simpleInteraction.out");
        try (var inputStream = new FileInputStream(inputFile);
             var printStream = new PrintStream(outputFile)) {

            System.setIn(inputStream);
            System.setOut(printStream);
            Main.main(ArrayUtils.EMPTY_STRING_ARRAY);
            assertTrue(FileUtils.contentEquals(ansFile, outputFile));
        }

    }

}