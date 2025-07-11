package ru.otus.processor.homework;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import ru.otus.model.Message;
import ru.otus.processor.homework.exception.EvenSecondException;

class ProcessorThrowsExceptionIfSecondIsEvenTest {

    private TimeProvider provider;
    private ProcessorThrowsExceptionIfSecondIsEven processor;
    private Message message;

    @BeforeEach
    void setUp() {
        provider = Mockito.mock(TimeProvider.class);
        processor = new ProcessorThrowsExceptionIfSecondIsEven(provider);
        message = Mockito.mock(Message.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 38, 16, 54, 0})
    void exceptionIfEventSecond(int second) {
        currentTimeMock(second);
        assertThrows(EvenSecondException.class, () -> processor.process(message));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 37, 15, 53, 29})
    void notExceptionIfOddSecond(int second) {
        currentTimeMock(second);
        assertDoesNotThrow(() -> processor.process(message));
    }

    private void currentTimeMock(int second) {
        when(provider.getCurrentTime()).thenReturn(LocalTime.of(1, 1, second));
    }
}
