package ru.otus.processor.homework;

import ru.otus.model.Message;
import ru.otus.processor.Processor;
import ru.otus.processor.homework.exception.EvenSecondException;

public class ProcessorThrowsExceptionIfSecondIsEven implements Processor {

    private final TimeProvider timeProvider;

    public ProcessorThrowsExceptionIfSecondIsEven(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public Message process(Message message) {
        int currentSecond = timeProvider.getCurrentSecond();
        if (currentSecond % 2 == 0) {
            throw new EvenSecondException("Second is even");
        }
        return message;
    }
}
