package ru.hse.kostya.java;

import java.util.LinkedList;
import java.util.List;

public class BlockingQueue<T> {

    private List<T> queue = new LinkedList<>();

    public synchronized void add(T element) {
        queue.add(element);
        if (queue.size() == 1) {
            notifyAll();
        }
    }

    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.remove(0);
    }

}
