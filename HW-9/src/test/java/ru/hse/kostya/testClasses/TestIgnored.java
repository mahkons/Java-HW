package ru.hse.kostya.testClasses;

import ru.hse.kostya.annotations.Test;

public class TestIgnored {

    @Test(ignore = "i don't like it")
    public void testIgnored() {
        throw new AssertionError("shouldn't get here");
    }
}
