package ru.hse.kostya.java;

import java.util.LinkedList;
import java.util.List;

/**
 * Parametrized queue,
 *  which contains elements of given type
 *  and synchronises on every add and take operation.
 */
public class BlockingQueue<T> {

    private List<T> queue = new LinkedList<>();

    /**
     * Adds element to the tail of queue;
     */
    public synchronized void add(T element) {
        queue.add(element);
        if (queue.size() == 1) {
            notifyAll();
        }
    }

    /**
     * Removes head element of the queue and returns it.
     * If queue is empty waits for add operation
     */
    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.remove(0);
    }

}
