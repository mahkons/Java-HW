package ru.hse.kostya.java;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReflectorTest {

    private static class SimplePrimitives {
        int a;
        byte b;
        char c;
        long d;
        boolean e;
        short f;
        double g;
        long h;
    }

    private static class FieldsModifiers {
        private int a;
        protected static final long b = 0;
    }

    private static class methodModifiers {
        private static int a() { return 0;}
        final int result() { return 0; }
    }

    private static class DefaultInitializingOfFinalFields {
        final int a = 0;
        final int[] b = null;
        final char c = '0';
        final String s = null;
        final List<Integer> l = null;
    }

    private static class WithSuperClass extends Exception {
        int a = 0;
    }

    private static class WithInterfaces implements Serializable, Comparable<Object> {
        public int compareTo(Object o) {
            return 0;
        }
    }

    private static class TestConstructors {
        final int some;
        private TestConstructors() { some = 0; }
        public TestConstructors(int a) { some = 0; };
        protected TestConstructors(String s, int some) { this.some = some; }
    }

    private static class TestMethods {
        int superMethod() { return 0; };
        int evenBetterOne(int arg) { return 0; }
        private void hardOne(int arg, List<String> variable) {}
    }

    private static class GenericFields<T extends List<Object>, S> {
        T a;
        S[] b;
        final List<S> l = null;
    }

    private static class GenericMethods<T, S> {
        T simple() { return null; }
        <E> T parametrized(E elem) { return null; }
        <E extends T> void complexParameter(E elem) {}
        <E extends List<S>, K> S hardOne(E elem) { return null; }
        <E, K> S withComplexArg(List<S> l) { return null; }
    }

    private static class WildCardTypes<T, S> {
        void simple(List<? super T> l) {}
        Collection<? extends T> withReturnValue(){ return null; }
        <E extends List<? super T>> T complexOne(Collection<? super Collection<? extends E>> l) { return null; }

    }

    static class NestedAndInnerClasses<T> {
        static class Nested {
            final int a = 0;
            protected int f() {return 0; }

            static class NestedInNested {
                static int b = 0;
                int otherFunction(int arg) {
                    return 0;
                }
            }
        }

        public class InnerClass {
            final int a = 0;
            int f() { return 0; }

        }
    }

    //private static class BigAndComplexClass
    //testing difference between classes
    //tests themselves

    private boolean writeToFileAndCompare(Class<?> clazz) throws IOException {
        Reflector.printStructure(clazz);
        var outputFile = new File(clazz.getSimpleName() + ".java");
        var expectedFile = new File("src/test/resources/" + clazz.getSimpleName() + ".java");
        return FileUtils.contentEquals(expectedFile, outputFile);
    }



}