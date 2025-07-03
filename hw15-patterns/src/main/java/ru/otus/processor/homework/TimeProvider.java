package ru.otus.processor.homework;

import java.time.LocalTime;

public interface TimeProvider {

    LocalTime getCurrentTime();
}
