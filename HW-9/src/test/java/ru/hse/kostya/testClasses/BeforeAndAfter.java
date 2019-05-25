package ru.hse.kostya.testClasses;

import ru.hse.kostya.annotations.After;
import ru.hse.kostya.annotations.Before;
import ru.hse.kostya.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class BeforeAndAfter {

    public static AtomicInteger beforeCount = new AtomicInteger(0);
    public static AtomicInteger afterCount = new AtomicInteger(0);

    @Before
    public void before() {
        beforeCount.incrementAndGet();
    }

    @After
    public void after() {
        afterCount.incrementAndGet();
    }

    @Test
    public void test1() {

    }

    @Test
    public void test2() {

    }

    @Test
    public void test3() {

    }


}
