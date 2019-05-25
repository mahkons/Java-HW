package ru.hse.kostya.testClasses;

import ru.hse.kostya.annotations.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class BeforeAndAfterClass {

    public static AtomicBoolean beforeFlag = new AtomicBoolean(false);
    public static AtomicBoolean afterFlag = new AtomicBoolean(false);

    @BeforeClass
    public void before() {
        beforeFlag.set(true);
    }

    @AfterClass
    public void after() {
        afterFlag.set(true);
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
