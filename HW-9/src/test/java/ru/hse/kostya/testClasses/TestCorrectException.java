package ru.hse.kostya.testClasses;

import ru.hse.kostya.annotations.Test;

public class TestCorrectException {

    @Test(expected = IndexOutOfBoundsException.class)
    public void correctException() {
        throw new IndexOutOfBoundsException("why not?");
    }
}
