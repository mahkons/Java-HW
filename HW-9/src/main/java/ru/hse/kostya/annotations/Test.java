package ru.hse.kostya.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with {@code Test} will be run by {@code TestsLauncher} class.
 * If {@code ignore} parameter is not empty, method won't invoke and ignore message will shown to user
 *  otherwise method will be invoked
 * If {@code expected} parameter is not {@code NoException.class}, which is default, method should throw
 * {@code Throwable} of {@code expected} class to pass, otherwise it should not throw any {@code Throwable}
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Test {

    class NoException extends Throwable {}

    String ignore() default "";
    Class<? extends Throwable> expected() default NoException.class;

}
