package ru.demo;

import ru.myframework.tester.annotation.After;
import ru.myframework.tester.annotation.Before;
import ru.myframework.tester.annotation.Test;

@SuppressWarnings({"java:S106", "java:S112"})
public class DemoClassTest {

    @Before
    void setUp() {
        System.out.println("start tests");
    }

    @Test
    void test1() {
        System.out.println("run test1");
    }

    @Test
    void test2() {
        System.out.println("run test2");
        throw new RuntimeException("test failed");
    }

    @Test
    void test3() {
        System.out.println("run test3");
    }

    @After
    void end() {
        System.out.println("finish tests");
    }
}
