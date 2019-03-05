package ru.hse.kostya.java;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
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
        float h;
    }

    private static class FieldsModifiers {
        private int a;
        protected static final long b = 0;
    }

    private static class MethodModifiers {
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

    private static class WildcardTypes<T, S> {
        void simple(List<? super T> l) {}
        Collection<? extends T> withReturnValue(){ return null; }
        <E extends List<? super T>> T complexOne(Collection<? super Collection<? extends E>> l) { return null; }

    }

    private static class NestedAndInnerClasses<T> {
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

    public static class ClassForDifference<T, K> {
        int a;
        private final String str = null;
        T[] some;
        List<Integer> listInteger;

        List<? super K> strangeList;
        List<? extends Object> niceOne;

        protected int b;

        private static void someMethod() {}
        private static void MethodWithArgsSame(List<? super Collection<? extends Object>> l) {}
        private static void MethodWithArgsDifferent(List<? super Collection<? extends Object>> l) {}

        private void methodWithOtherModifiers() {}

        private int methodWithReturnValue() { return 0; }

    }

    public static class ClassForDifferenceAnotherOne<T> {
        int a;
        private String str = null;
        T[] some;
        List<Integer> listNotOfInteger;

        List<?> strangeList;
        List<?> niceOne;

        private int b;

        private static void someMethod() {}
        private static void MethodWithArgsSame(List<? super Collection<?>> l) {}
        private static void MethodWithArgsDifferent(List<? super Collection<? extends List>> l) {}

        public void MethodWithOtherModifiers() {}

        private T[] methodWithReturnValue() { return null; }



    }

    @Test
    void simplePrimitives() throws IOException {
        assertTrue(writeToFileAndCheckForEquality(SimplePrimitives.class));
    }

    @Test
    void fieldsModifiers() throws IOException {
        assertTrue(writeToFileAndCheckForEquality(FieldsModifiers.class));
    }

    @Test
    void methodModifiers() throws IOException {
        assertTrue(writeToFileAndCheckForEquality(MethodModifiers.class));
    }

    @Test
    void defaultInitialising() throws IOException {
        assertTrue(writeToFileAndCheckForEquality(DefaultInitializingOfFinalFields.class));
    }

    @Test
    void withSuperClass() throws IOException {
        assertTrue(writeToFileAndCheckForEquality(WithSuperClass.class));
    }

    @Test
    void withInterfaces() throws IOException {
        assertTrue(writeToFileAndCheckForEquality(WithInterfaces.class));
    }

    @Test
    void constructors() throws IOException {
        assertTrue(writeToFileAndCheckForEquality(TestConstructors.class));
    }

    @Test
    void methods() throws IOException {
        assertTrue(writeToFileAndCheckForEquality(TestMethods.class));
    }

    @Test
    void genericFields() throws IOException {
        assertTrue(writeToFileAndCheckForEquality(GenericFields.class));
    }

    @Test
    void genericMethods() throws IOException {
        assertTrue(writeToFileAndCheckForEquality(GenericMethods.class));
    }

    @Test
    void wildcardType() throws IOException {
        assertTrue(writeToFileAndCheckForEquality(WildcardTypes.class));
    }

    @Test
    void nestedAndInnerClasses() throws IOException {
        assertTrue(writeToFileAndCheckForEquality(NestedAndInnerClasses.class));
    }

    @Test
    void difference() throws IOException {
        var outputFile = new File("src/test/resources/DiffClass.out");
        var ansFile = new File("src/test/resources/DiffClass.ans");
        try (var printStream = new PrintStream(outputFile)) {
            System.setOut(printStream);
            Reflector.diffClasses(ClassForDifference.class, ClassForDifferenceAnotherOne.class);
            assertTrue(FileUtils.contentEquals(ansFile, outputFile));
        }

    }


    private boolean writeToFileAndCheckForEquality(Class<?> clazz) throws IOException {
        Reflector.printStructure(clazz);
        var outputFile = new File(clazz.getSimpleName() + ".java");
        var expectedFile = new File("src/test/resources/" + clazz.getSimpleName() + ".ans");
        return FileUtils.contentEquals(expectedFile, outputFile);
    }



}