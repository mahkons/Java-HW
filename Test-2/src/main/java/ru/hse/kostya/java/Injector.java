package ru.hse.kostya.java;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;


/**
 * Utility class that initialize objects
 *      with exactly one parameter using given Classes, which initialized recursively.
 */
public class Injector {


    /** Needs to check existence of cycle dependencies */
    private static Set<Class<?>> initializingNow = new HashSet<>();
    /** Needed to initialize every class once only */
    private static Map<Class<?>, Object> initializedAlready = new HashMap<>();

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     *
     * @throws ClassNotFoundException if rootClass or one of ImplementationClasses it depends from cannot be bound
     * @throws AmbiguousImplementationException if more than one class can be used as dependency
     * @throws ImplementationNotFoundException if there is no class that can be used as dependency
     * @throws InjectionCycleException if classes rootClass depends from form cyclic dependency
     */
    public static Object initialize(@NotNull String rootClassName, @NotNull List<String> implementationClassNames)
            throws ClassNotFoundException, IllegalAccessException, AmbiguousImplementationException, ImplementationNotFoundException,
            InstantiationException, InjectionCycleException, InvocationTargetException {

        Class<?> clazz = Class.forName(rootClassName);
        var implementationClasses = new ArrayList<Class<?>>();
        for (String className : implementationClassNames) {
            implementationClasses.add(Class.forName(className));
        }
        final Object initializedObject = initializeRecursively(clazz, implementationClasses);
        initializingNow.clear();
        initializedAlready.clear();
        return initializedObject;
    }

    private static Object initializeRecursively(Class<?> clazz, final List<Class<?>> implementationClasses)
            throws AmbiguousImplementationException, ImplementationNotFoundException, InjectionCycleException,
            IllegalAccessException, InvocationTargetException, InstantiationException {

        if (clazz.getDeclaredConstructors().length != 1) {
            throw new IllegalStateException("Class " + clazz.getSimpleName() + "ought to have exactly one constructor");
        }

        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Type[] parameterTypes = constructor.getGenericParameterTypes();
        List<Object> initializedParameters = new ArrayList<>();
        for (Type type : parameterTypes) {
            if (type instanceof Class) {
                var typeClass = (Class<?>)type;
                if (initializedAlready.containsKey(typeClass)) {
                    initializedParameters.add(initializedAlready.get(typeClass));
                    continue;
                }

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
                initializedParameters.add(typeParameter);
                endInitialization(implementationThatTypeClass, typeParameter);

            } else {
                throw new IllegalStateException("Parameter " + type.getTypeName() + " is not just some class or interface");
            }

        }
        return constructor.newInstance(initializedParameters.toArray());

    }

    /**
     * All actions needed to be done before initializing class.
     * Adding class to set of initializing now
     */
    private static void startInitialization(Class<?> clazz) {
        initializingNow.add(clazz);
    }

    /**
     * All actions needed to be done after initializing class.
     * Removes class from set of initializing now
     * Adds class instance to initialized set
     */
    private static void endInitialization(Class<?> clazz, Object classInstance) {
        assert initializingNow.contains(clazz);

        initializingNow.remove(clazz);
        initializedAlready.put(clazz, classInstance);
    }
}
