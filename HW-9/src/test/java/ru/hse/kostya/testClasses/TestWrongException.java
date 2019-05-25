package ru.hse.kostya.testClasses;

import ru.hse.kostya.annotations.Test;

public class TestWrongException {

    @Test(expected = IndexOutOfBoundsException.class)
    public void wrongException() {
        throw new IllegalStateException("WRONG!");
    }
}
