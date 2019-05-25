package ru.hse.kostya;

import ru.hse.kostya.annotations.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

/**
 * Class for loading compiled classes and launching there tests methods.
 * Tests methods are annotated with {@code Test}
 * Annotations {@code BeforeClass, AfterClass, Before, After} used according to there documentation
 * Main method takes path to {@code .class or .jar} file as an argument and launches all tests
 *  in those classes and outputs information about invocation result.
 * Tests run parallel in number of available processors threads
 */
public class TestsLauncher {

    /**
     * Number of threads in ThreadPool, which is used for test methods invocation.
     */
    private static int parallelism = Runtime.getRuntime().availableProcessors();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(parallelism);

    /**
     * Loads class on the specified path.
     */
    private static Class<?> loadClass(Path path) throws MalformedURLException, ClassNotFoundException {
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { path.getParent().toUri().toURL() });
        String fileName = path.getFileName().toString();
        fileName = fileName.substring(0, fileName.length() - ".class".length());
        return classLoader.loadClass(fileName);
    }

    /**
     * Loads all classes from specified jar file.
     */
    private static List<Class<?>> loadClassesFromJar(Path path) throws IOException, ClassNotFoundException {
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { path.getParent().toUri().toURL() });

        List<Class<?>> classes = new ArrayList<>();
        var jarFile = new JarInputStream(new FileInputStream(path.toFile()));
        JarEntry jarEntry;
        while ((jarEntry = jarFile.getNextJarEntry()) != null) {
            if (jarEntry.getName().endsWith(".class")) {
                classes.add(classLoader.loadClass(jarEntry.getName()));
            }
        }
        return classes;
    }

    /**
     * Takes path to exactly one {@code .class or .jar} file and launches all tests on the specified path.
     * Test methods should be annotated with {@code Test} annotation.
     * There is a possibility to force some other methods be invoked before/after all class
     *  initialisation or each test method invocation. Annotation from {@code ru.hse.kostya.annotations} should
     *  be used to achieve that functionality
     *  Class should have an accessible constructor without parameters
     *  If for any class it's initialization or running methods annotated with {@code AfterClass or BeforeClass}
     *      will fail, program will exit immediately
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected one argument: path to file with tests");
        }
        Path path = Paths.get(args[0]);
        System.out.println(args[0]);
        if (!path.toFile().exists()) {
            throw new IllegalArgumentException("File does not exist");
        }
        if (!path.toString().endsWith(".class") && !path.toString().endsWith(".jar")) {
            throw new IllegalArgumentException("Test should be .class or .java file");
        }

        List<Class<?>> classes = new ArrayList<>();

        if (path.toString().endsWith(".class")) {
            try {
                classes.add(loadClass(path));
            } catch (ClassNotFoundException e) {
                System.out.println("Cannot find a class on the specified path: " + e.getMessage());
                return;
            } catch (MalformedURLException e) {
                System.out.println("Given path seems to be incorrect: " + e.getMessage());
                return;
            }
        } else {
            try {
                classes.addAll(loadClassesFromJar(path));
            } catch (IOException e) {
                System.out.println("Failed to read jar file content: " + e.getMessage());
                return;
            } catch (ClassNotFoundException e) {
                System.out.println("Failed to load class file from jar: " + e.getMessage());
                return;
            }
        }


        for (Class<?> clazz : classes) {
            System.out.println("Invocation of " + clazz.getName() + "tests started");
            try {
                TestOfClassResult testResult = invokeTestMethods(clazz);
                System.out.println("Time gone: " + testResult.getTimeGone() + " milliseconds.");
                System.out.println("Tests passed " + testResult.getSuccess());
                System.out.println("Tests failed " + testResult.getFailed());
                System.out.println("Tests ignored " + testResult.getIgnored());
            } catch (InterruptedException e) {
                System.out.println("Thread pool worker was interrupted during method invocation");
                System.exit(7);
            } catch (ExecutionException e) {
                System.out.println("Unexpected exception during test method invocation");
                System.exit(8);
            }
            System.out.println("Invocation of " + clazz.getName() + "tests ended");
        }
    }

    /**
     * Gets all accessible methods from given testClass, which are annotated with specified annotation.
     */
    private static List<Method> getTestingMethods(Class<?> testClass, Class<? extends Annotation> annotation) {
        return Arrays.stream(testClass.getMethods())
                .filter(x -> x.isAnnotationPresent(annotation)).collect(Collectors.toList());
    }

    /**
     * Invokes all tests method from class.
     * Outputs information about invocation
     * There may be created few instances of class and different tests will run on them concurrently
     * Returns {@code TestOfClassResult} which contains amount of passes, failed and ignored tests
     *  and time used for invocation
     */
    public static TestOfClassResult invokeTestMethods(Class<?> testClass)
            throws InterruptedException, ExecutionException {

        List<Method> beforeClassMethods = getTestingMethods(testClass, BeforeClass.class);
        List<Method> afterClassMethods = getTestingMethods(testClass, AfterClass.class);

        Map<Long, Object> classByThread = new ConcurrentHashMap<>();

        List<Method> beforeTestMethod = getTestingMethods(testClass, Before.class);
        List<Method> testMethods = getTestingMethods(testClass, Test.class);
        List<Method> afterTestMethod = getTestingMethods(testClass, After.class);


        List<InvokeTask> tasks = testMethods.stream()
                .map(testMethod -> new InvokeTask(testMethod, classByThread, testClass,
                        beforeClassMethods, beforeTestMethod, afterTestMethod))
                .collect(Collectors.toList());

        long startMillis = System.currentTimeMillis();
        List<Future<TestInvocationResult>> invocationResult = threadPool.invokeAll(tasks);
        var testResult = new TestOfClassResult();

        for (Future<TestInvocationResult> future : invocationResult) {
            System.out.println(future.get().getMessage());
            switch (future.get().getInvocationCode()) {
                case FAIL:
                    testResult.addFailed();
                    break;
                case IGNORE:
                    testResult.addIgnored();
                    break;
                case SUCCESS:
                    testResult.addSuccess();
                    break;
            }
        }
        for (Object classInstance : classByThread.values()) {
            afterClassMethods.forEach(afterClassMethod -> invokeClassMethod(afterClassMethod,
                    classInstance, "AfterClass", 5, 6));
        }
        long stopMillis = System.currentTimeMillis();
        testResult.setTimeGone(stopMillis - startMillis);

        return testResult;
    }

    /**
     * Invokes method on given object and exits in case of fail.
     */
    private static void invokeClassMethod(Method method, Object testObject, String annotationName,
                                          int exitCodeIllegalAccess, int exitCodeInvocationException) {
        try {
            method.invoke(testObject);
        } catch (IllegalAccessException e) {
            System.out.println("Cannot access method annotated with " + annotationName
                    + ": " + e.getMessage());
            System.exit(exitCodeIllegalAccess);
        } catch (InvocationTargetException e) {
            System.out.println("Exception occurred during running method annotated with "
                    + annotationName + ": " + e.getMessage());
            System.exit(exitCodeInvocationException);
        }
    }

    /**
     * Method invocation task, used in threadPool in {@code invokeTestMethods} method.
     * Returns {@code TestInvocationResult} instance with information about invocation
     */
    private static class InvokeTask implements Callable<TestInvocationResult> {

        //thread id's are unique inside one jvm, unless we specify them.
        // And ThreadPool doesn't specify them.
        private Map<Long, Object> classByThread;
        private Class<?> testClass;
        private List<Method> beforeClassList;

        private List<Method> beforeList;
        private Method testMethod;
        private List<Method> afterList;

        private StringBuilder invocationResult = new StringBuilder();
        private boolean success = true;
        private long startMillis;
        private long stopMillis;

        private InvokeTask(Method testMethod, Map<Long, Object> classByThread,
                          Class<?> testClass, List<Method> beforeClassList,
                          List<Method> beforeList, List<Method> afterList) {
            this.classByThread = classByThread;
            this.testMethod = testMethod;
            this.testClass = testClass;
            this.beforeClassList = beforeClassList;
            this.beforeList = beforeList;
            this.afterList = afterList;
        }

        /**
         * Invokes method.
         * In case of fail writes reason to {@code invocationResult} variable
         *  and sets {@code} success to {@code false}
         */
        private void invokeMethod(Method method, Object testObject, String annotationName) {
            try {
                method.invoke(testObject);
            } catch (IllegalAccessException e) {
                invocationResult.append("Cannot access method annotated with ")
                        .append(annotationName).append(": ").append(e.getMessage()).append("\n");
                success = false;
            } catch (InvocationTargetException e) {
                invocationResult.append("Failed to run method annotated with ")
                        .append(annotationName).append(": ").append(e.getMessage()).append("\n");
                success = false;
            }
        }

        @Override
        public TestInvocationResult call() {
            Test testAnnotation = testMethod.getAnnotation(Test.class);
            if (!testAnnotation.ignore().equals("")) {
                return  new TestInvocationResult("For class " + testClass.getName() + " invocation of test "
                        + testMethod.getName() + " ignored with message: " + testAnnotation.ignore(),
                        TestInvocationResult.InvocationCode.IGNORE, 0);
            }

            Object testObject = classByThread.computeIfAbsent(Thread.currentThread().getId(), i -> {
                Object object = null;
                try {
                    Constructor<?> constructor = testClass.getConstructor();
                    object = constructor.newInstance();
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    System.out.println(testClass.getName() + " has no available constructor without parameters");
                    System.exit(1);
                } catch (InstantiationException | InvocationTargetException e) {
                    System.out.println("Failed to make an instance of class: " + testClass.getName()
                            + " due to " + e.getMessage());
                    System.exit(2);
                }

                for (Method beforeClassMethod : beforeClassList) {
                   invokeClassMethod(beforeClassMethod, object, "BeforeClass", 3, 4);
                }
                return object;
            });

            startMillis = System.currentTimeMillis();
            beforeList.forEach(x -> invokeMethod(x, testObject, "Before"));

            try {
                testMethod.invoke(testObject);
                if (testAnnotation.expected() != Test.NoException.class) {
                    invocationResult.append("Test finished without expected exception\n");
                    success = false;
                } else {
                    invocationResult.append("Test finished successfully without exception\n");
                }
            } catch (IllegalAccessException e) {
                invocationResult.append("Failed to access testMethod: ").append(e.getMessage()).append("\n");
                success = false;
            } catch (InvocationTargetException e) {
                if (testAnnotation.expected() != e.getTargetException().getClass()) {
                    invocationResult.append("Test finished with wrong exception. Expected ")
                            .append(testAnnotation.expected().getName()).append(". Received: ")
                            .append(e.getTargetException().getClass().getName()).append("\n");
                    success = false;
                } else {
                    invocationResult.append("Test finished successfully with expected exception: ")
                            .append(testAnnotation.expected().getName()).append("\n");
                }
            }

            afterList.forEach(x -> invokeMethod(x, testObject, "After"));
            stopMillis = System.currentTimeMillis();

            final String fullMessage = "For class " + testClass.getName()
                    + ". Invocation of test " + testMethod.getName()
                    + (success ? " finished successfully\n" : " failed\n")
                    + "With message: " + invocationResult
                    + "In time: " + (stopMillis - startMillis) + " milliseconds.";
            return new TestInvocationResult(fullMessage,
                    (success ? TestInvocationResult.InvocationCode.SUCCESS
                            : TestInvocationResult.InvocationCode.FAIL),
                    stopMillis - startMillis);
        }
    }

    /**
     * Keeps information about test method invocation.
     * Message, invocation code{@code SUCCESS, FAIL or PASS} and time used for invocation
     */
    private static class TestInvocationResult {

        public enum InvocationCode {
            SUCCESS,
            FAIL,
            IGNORE;
        }

        private String message;
        private InvocationCode invocationCode;
        private long timeMillis;

        public TestInvocationResult(String message, InvocationCode invocationCode, long timeMillis) {
            this.message = message;
            this.invocationCode = invocationCode;
            this.timeMillis = timeMillis;
        }

        public String getMessage() {
            return message;
        }

        public InvocationCode getInvocationCode() {
            return invocationCode;
        }

    }

    /**
     * Keeps information about invocation of all methods in class.
     * Keeps number of succeeded, failed and ignored methods
     *  and time used for invocation of all methods
     */
    public static class TestOfClassResult {
        private int success = 0;
        private int failed = 0;
        private int ignored = 0;

        private long timeGone;

        public void addSuccess() {
            success++;
        }

        public void addFailed() {
            failed++;
        }

        public void addIgnored() {
            ignored++;
        }

        public int getSuccess() {
            return success;
        }

        public int getFailed() {
            return failed;
        }

        public int getIgnored() {
            return ignored;
        }

        public long getTimeGone() {
            return timeGone;
        }

        public void setTimeGone(long timeGone) {
            this.timeGone = timeGone;
        }
    }

}
