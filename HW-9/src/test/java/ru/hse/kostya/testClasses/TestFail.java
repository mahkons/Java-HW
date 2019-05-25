package ru.hse.kostya.testClasses;

import ru.hse.kostya.annotations.Test;

public class TestFail {

    @Test
    public void failTest() {
        throw new AssertionError("failed test");
    }
}
