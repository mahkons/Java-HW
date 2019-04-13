package ru.hse.kostya.java;

import java.util.function.Function;

/**
 * Interface for tasks that accepted for execution in some ThreadPool.
 */
public interface LightFuture<T> {

    /** Returns true if task is executed and false otherwise */
    boolean isReady();

    /**
     * Returns result of task execution.
     * Waits if it is not executed right now
     * @throws LightExecutionException when any Exception occurred during task execution
     * @throws InterruptedException if current Thread was interrupted while waiting for task result
     */
    T get() throws LightExecutionException, InterruptedException;

    /**
     * Add task to threadPool.
     * Task is to apply function to result of this LightFuture
     */
    <R> LightFuture<R> thenApply(Function<? super T, R> function);
}
