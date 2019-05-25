package ru.hse.kostya.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Methods annotated with {@code AfterClass} will
 *  be launched before running tests, once for every test object.
 * If there are few of such methods, they may run in arbitrary order.
 * If there no tests, this method won't run
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface BeforeClass {
}
