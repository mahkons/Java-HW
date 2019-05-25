package ru.hse.kostya;

import org.junit.jupiter.api.Test;
import ru.hse.kostya.testClasses.*;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class TestsLauncherTest {

    @Test
    void emptyClass() throws Exception {
        TestsLauncher.TestOfClassResult testsResult = TestsLauncher.invokeTestMethods(Empty.class);
        assertEquals(testsResult.getFailed(), 0);
        assertEquals(testsResult.getSuccess(), 0);
        assertEquals(testsResult.getIgnored(), 0);
    }

    @Test
    void afterAndBefore() throws Exception {
        TestsLauncher.TestOfClassResult testsResult = TestsLauncher.invokeTestMethods(BeforeAndAfter.class);
        assertEquals(testsResult.getFailed(), 0);
        assertEquals(testsResult.getSuccess(), 3);
        assertEquals(testsResult.getIgnored(), 0);

        assertEquals(3, BeforeAndAfter.beforeCount.get());
        assertEquals(3, BeforeAndAfter.afterCount.get());
    }

    @Test
    void beforeAndAfterClass() throws Exception {
        TestsLauncher.TestOfClassResult testsResult = TestsLauncher.invokeTestMethods(BeforeAndAfterClass.class);
        assertEquals(testsResult.getFailed(), 0);
        assertEquals(testsResult.getSuccess(), 3);
        assertEquals(testsResult.getIgnored(), 0);

        assertTrue(BeforeAndAfterClass.beforeFlag.get());
        assertTrue(BeforeAndAfterClass.afterFlag.get());
    }

    @Test
    void testCorrectException() throws Exception {
        TestsLauncher.TestOfClassResult testsResult = TestsLauncher.invokeTestMethods(TestCorrectException.class);
        assertEquals(testsResult.getFailed(), 0);
        assertEquals(testsResult.getSuccess(), 1);
        assertEquals(testsResult.getIgnored(), 0);
    }

    @Test
    void testFail() throws Exception {
        TestsLauncher.TestOfClassResult testsResult = TestsLauncher.invokeTestMethods(TestFail.class);
        assertEquals(testsResult.getFailed(), 1);
        assertEquals(testsResult.getSuccess(), 0);
        assertEquals(testsResult.getIgnored(), 0);
    }

    @Test
    void testIgnored() throws Exception {
        TestsLauncher.TestOfClassResult testsResult = TestsLauncher.invokeTestMethods(TestIgnored.class);
        assertEquals(testsResult.getFailed(), 0);
        assertEquals(testsResult.getSuccess(), 0);
        assertEquals(testsResult.getIgnored(), 1);
    }

    @Test
    void testPass() throws Exception {
        TestsLauncher.TestOfClassResult testsResult = TestsLauncher.invokeTestMethods(TestPass.class);
        assertEquals(testsResult.getFailed(), 0);
        assertEquals(testsResult.getSuccess(), 2);
        assertEquals(testsResult.getIgnored(), 0);
    }

    @Test
    void testWrongException() throws Exception {
        TestsLauncher.TestOfClassResult testsResult = TestsLauncher.invokeTestMethods(TestWrongException.class);
        assertEquals(testsResult.getFailed(), 1);
        assertEquals(testsResult.getSuccess(), 0);
        assertEquals(testsResult.getIgnored(), 0);
    }

}