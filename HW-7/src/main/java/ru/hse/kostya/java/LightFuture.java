package ru.hse.kostya.java;

import java.util.function.Function;

public interface LightFuture<T> {

    boolean isReady();

    T get() throws LightExecutionException;

    <R> LightFuture<R> thenApply(Function<? super T, R> function);
}
