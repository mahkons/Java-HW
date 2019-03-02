package ru.hse.kostya.java;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReflectorTest {

    private static class JustClass<T> implements Serializable {

        private void doIt(List<? super String> value) {
            return;
        }

        public String str;
        protected int[] a;
        private T[] superType;
    }

    protected static class SimpleClass<T extends String & List, S> extends AbstractList<S> implements Comparable<T>, Serializable {

        private final T PrivateFinalValue = null;
        public static Object CONST = " ";

        @Override
        public int compareTo(@NotNull T o) {
            return 0;
        }

        @Override
        public S get(int index) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }
    }

    @Test
    void SimpleClass() throws IOException {
        Reflector.printStructure(SimpleClass.class);
    }

    @Test
    void JustClass() throws IOException {
        Reflector.printStructure(JustClass.class);
    }


}