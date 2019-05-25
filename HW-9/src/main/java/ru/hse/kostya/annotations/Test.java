package ru.hse.kostya.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Test {

    class NoException extends Throwable {};

    String ignore() default "";
    Class<? extends Throwable> expected() default NoException.class;

}
