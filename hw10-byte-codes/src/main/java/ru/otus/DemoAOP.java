package ru.otus;

public class DemoAOP {
    public static void main(String[] args) {
        TestLoggingInterface myClass = Ioc.createClass(new TestLogging());
        myClass.calculation(1);
        myClass.calculation(1, 2);
        myClass.calculation(1, "abc");
        myClass.calculation(9);
    }
}
