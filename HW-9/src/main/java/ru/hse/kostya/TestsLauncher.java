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

public class TestsLauncher {

    private static int parallelism = Runtime.getRuntime().availableProcessors();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(parallelism);

    private static Class<?> loadClass(Path path) throws MalformedURLException, ClassNotFoundException {
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { path.toUri().toURL() });
        return classLoader.loadClass(path.toString());
    }

    private static List<Class<?>> loadClassesFromJar(Path path) throws IOException, ClassNotFoundException {
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { path.toUri().toURL() });

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

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected one argument: path to file with tests");
        }
        Path path = Paths.get(args[0]);
        if (!path.toFile().exists()) {
            throw new IllegalArgumentException("File does not exist");
        }
        if (!path.endsWith(".class") && !path.endsWith(".jar")) {
            throw new IllegalArgumentException("Test should be .class or .java file");
        }

        List<Class<?>> classes = new ArrayList<>();

        if (path.endsWith(".class")) {
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


        for (Class<?> aClass : classes) {
            System.out.println("Invocation of " + aClass.getName() + "methods started");
            try {
                invokeTestMethods(aClass);
            } catch (InterruptedException e) {
                System.out.println("Thread pool worker was interrupted during method invocation");
                System.exit(7);
            } catch (ExecutionException e) {
                System.out.println("Unexpected exception during test method invocation");
                System.exit(8);
            }
            System.out.println("Invocation of " + aClass.getName() + "methods endeds");
        }
    }

    private static List<Method> getTestingMethods(Class<?> testClass, Class<? extends Annotation> annotation) {
        return Arrays.stream(testClass.getMethods())
                .filter(x -> x.isAnnotationPresent(annotation)).collect(Collectors.toList());
    }

    public static void invokeTestMethods(Class<?> testClass) throws InterruptedException, ExecutionException {

        List<Method> beforeClassMethods = getTestingMethods(testClass, BeforeClass.class);
        List<Method> afterClassMethods = getTestingMethods(testClass, AfterClass.class);

        Map<Long, Object> classByThread = new HashMap<>();

        List<Method> beforeTestMethod = getTestingMethods(testClass, Before.class);
        List<Method> testMethods = getTestingMethods(testClass, Test.class);
        List<Method> afterTestMethod = getTestingMethods(testClass, After.class);


        List<InvokeTask> tasks = testMethods.stream()
                .map(testMethod -> new InvokeTask(testMethod, classByThread, testClass, beforeClassMethods, beforeTestMethod, afterTestMethod))
                .collect(Collectors.toList());

        List<Future<String>> invocationResult = threadPool.invokeAll(tasks);
        for (Future<String> future : invocationResult) {
            System.out.println(future.get());
        }
        for (Object classInstance : classByThread.values()) {
            afterClassMethods.forEach(afterClassMethod -> invokeClassMethod(afterClassMethod, classInstance, "AfterClass", 5, 6));
        }
    }

    private static void invokeClassMethod(Method method, Object testObject, String annotationName, int exitCodeIllegalAccess, int exitCodeInvocationException) {
        try {
            method.invoke(testObject);
        } catch (IllegalAccessException e) {
            System.out.println("Cannot access method annotated with " + annotationName + ": " + e.getMessage());
            System.exit(exitCodeIllegalAccess);
        } catch (InvocationTargetException e) {
            System.out.println("Exception occurred during running ethod annotated with " + annotationName + ": " + e.getMessage());
            System.exit(exitCodeInvocationException);
        }
    }

    private static class InvokeTask implements Callable<String> {

        //thread id's are unique in given jvm, unless we specify them. And ThreadPool don't.
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

        public InvokeTask(Method testMethod, Map<Long, Object> classByThread,
                          Class<?> testClass, List<Method> beforeClassList,
                          List<Method> beforeList, List<Method> afterList) {
            this.classByThread = classByThread;
            this.testMethod = testMethod;
            this.testClass = testClass;
            this.beforeClassList = beforeClassList;
            this.beforeList = beforeList;
            this.afterList = afterList;
        }

        private void invokeMethod(Method method, Object testObject, String annotationName) {
            try {
                method.invoke(testObject);
            } catch (IllegalAccessException e) {
                invocationResult.append("Cannot access method annotated with ").append(annotationName).append(": ").append(e.getMessage()).append("\n");
                success = false;
            } catch (InvocationTargetException e) {
                invocationResult.append("Failed to run method annotated with ").append(annotationName).append(": ").append(e.getMessage()).append("\n");
                success = false;
            }
        }

        @Override
        public String call() {
            Test testAnnotation = testMethod.getAnnotation(Test.class);
            if (!testAnnotation.ignore().equals("")) {
                return  "For class " + testClass.getName() + " invocation of test "
                        + testMethod.getName() + " ignored with message: " + testAnnotation.ignore();
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
                    System.out.println("Failed to make an instance of class: " + testClass.getName() + " due to " + e.getMessage());
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
                    invocationResult.append("Test finished with wrong exception. Expected ").append(testAnnotation.expected().getName())
                            .append(". Received: ").append(e.getTargetException().getClass().getName()).append("\n");
                    success = false;
                } else {
                    invocationResult.append("Test finished successfully with expected exception: ").append(testAnnotation.expected().getName()).append("\n");
                }
            }

            afterList.forEach(x -> invokeMethod(x, testObject, "After"));
            stopMillis = System.currentTimeMillis();

            final String fullMessage = "For class " + testClass.getName()
                    + ". Invocation of test " + testMethod.getName()
                    + (success ? " finished successfully\n" : " failed\n")
                    + "With message: " + invocationResult
                    + "\nIn time: " + (stopMillis - startMillis) + " milliseconds.";
            return fullMessage;
        }
    }

}
