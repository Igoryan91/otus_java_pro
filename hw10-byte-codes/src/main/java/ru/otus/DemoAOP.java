package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoAOP {
    private static final Logger log = LoggerFactory.getLogger(DemoAOP.class);

    public static void main(String[] args) {
        TestLoggingInterface myClass = Ioc.createClass(new TestLogging());
        myClass.calculation(1);
        myClass.calculation(1, 2);
        myClass.calculation(1, "abc");
        myClass.calculation(9);
    }
}
