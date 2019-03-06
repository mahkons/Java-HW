package ru.hse.kostya.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Injector {

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */

    private static List<Class<?>> initializingNow = new ArrayList<>();

    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        Class<?> clazz = Class.forName(rootClassName);
        var implementationClasses = new ArrayList<Class<?>>();
        for (String className : implementationClassNames) {
            implementationClasses.add(Class.forName(className));
        }
        return initializeRecursively(clazz, implementationClasses);
    }

    private static Object initializeRecursively(Class<?> clazz, final List<Class<?>> implementationClasses) throws Exception {
        if (clazz.getDeclaredConstructors().length != 1) {
            throw new IllegalStateException("Class " + clazz.getSimpleName() + "ought to have exactly one constructor");
        }

        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        Type[] parameterTypes = constructor.getGenericParameterTypes();
        List<Object> initializedParameters = new ArrayList<>();
        for (Type type : parameterTypes) {
            if (type instanceof Class) {
                var typeClass = (Class<?>)type;
                Class<?> implementationThatTypeClass = null;
                for (Class<?> someClass : implementationClasses) {
                    if (typeClass.isAssignableFrom(someClass)) {
                        if (implementationThatTypeClass != null) {
                            throw new AmbiguousImplementationException();
                        }
                        implementationThatTypeClass = someClass;
                    }
                }

                if (implementationThatTypeClass == null) {
                    throw new ImplementationNotFoundException();
                }
                if (initializingNow.contains(implementationThatTypeClass)) {
                    throw new InjectionCycleException();
                }

                startInitialization(implementationThatTypeClass);
                Object typeParameter = initializeRecursively(implementationThatTypeClass, implementationClasses);
                endInitialization(implementationThatTypeClass);

            } else {
                throw new IllegalStateException("Parameter " + type.getTypeName() +" is not just some class");
            }

        }
        return constructor.newInstance(initializedParameters.toArray());

    }

    private static void startInitialization(Class<?> clazz) {
        initializingNow.add(clazz);
    }

    private static void endInitialization(Class<?> clazz) {
        assert !initializingNow.isEmpty();
        assert initializingNow.get(initializingNow.size() - 1) == clazz;
        initializingNow.remove(initializingNow.size() - 1);
    }
}
