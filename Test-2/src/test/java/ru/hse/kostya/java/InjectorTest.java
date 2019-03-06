package ru.hse.kostya.java;

import org.junit.Test;
import task.testClasses.ClassWithOneClassDependency;
import task.testClasses.ClassWithOneInterfaceDependency;
import task.testClasses.ClassWithoutDependencies;
import task.testClasses.InterfaceImpl;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class InjectorTest {

    @Test
    public void injectorShouldInitializeClassWithoutDependencies()
            throws Exception {
        Object object = Injector.initialize("task.testClasses.ClassWithoutDependencies", Collections.emptyList());
        assertTrue(object instanceof ClassWithoutDependencies);
    }

    @Test
    public void injectorShouldInitializeClassWithOneClassDependency()
            throws Exception {
        Object object = Injector.initialize(
                "task.testClasses.ClassWithOneClassDependency",
                Collections.singletonList("task.testClasses.ClassWithoutDependencies")
        );
        assertTrue(object instanceof ClassWithOneClassDependency);
        ClassWithOneClassDependency instance = (ClassWithOneClassDependency) object;
        assertTrue(instance.dependency != null);
    }

    @Test
    public void injectorShouldInitializeClassWithOneInterfaceDependency()
            throws Exception {
        Object object = Injector.initialize(
                "task.testClasses.ClassWithOneInterfaceDependency",
                Collections.singletonList("task.testClasses.InterfaceImpl")
        );
        assertTrue(object instanceof ClassWithOneInterfaceDependency);
        ClassWithOneInterfaceDependency instance = (ClassWithOneInterfaceDependency) object;
        assertTrue(instance.dependency instanceof InterfaceImpl);
    }

    @Test
    public void injectorWithCycleDependencies() throws Exception {
        assertThrows(InjectionCycleException.class, () -> {
            Injector.initialize("task.testClasses.ClassFirstDependsFromSecond",
                    List.of("task.testClasses.ClassFirstDependsFromSecond", "task.testClasses.ClassSecondDependsFromFirst"));
        });
    }

    @Test
    public void injectorWithAmbiguousChoose() throws Exception {
        assertThrows(AmbiguousImplementationException.class, () -> {
            Injector.initialize("task.testClasses.ClassWithOneInterfaceDependency",
                    List.of("task.testClasses.InterfaceImpl", "task.testClasses.SecondInterfaceImpl"));
        });
    }

    @Test
    public void injectorWithNoImplementation() throws Exception {
        assertThrows(ImplementationNotFoundException.class, () -> {
            Injector.initialize("task.testClasses.ClassWithOneInterfaceDependency",
                    List.of("task.testClasses.ClassWithOneInterfaceDependency", "task.testClasses.ClassFirstDependsFromSecond"));
        });
    }

    @Test
    public void injectorCheckInitializingOnlyOnce() throws Exception {
        Injector.initialize("task.testClasses.TwoDependenciesFromCounter",
                List.of("task.testClasses.InitializingOnceOnly"));
    }
}