package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLogging implements TestLoggingInterface {
    private final Logger log = LoggerFactory.getLogger(TestLogging.class);

    @Log
    @Override
    public void calculation(int i) {
        log.info("int = {}", i);
    }

    @Override
    public void calculation(int i, int j) {
        log.info("int = {}, int = {}", i, j);
    }

    @Override
    public void calculation(int i, String s) {
        log.info("int = {}, string = {}", i, s);
    }
}
