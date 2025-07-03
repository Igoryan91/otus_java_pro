package ru.otus.processor.homework;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import ru.otus.model.Message;
import ru.otus.processor.homework.exception.EvenSecondException;

class ProcessorThrowsExceptionIfSecondIsEvenTest {

    @ParameterizedTest
    @ValueSource(ints = {2, 38, 16, 54, 0})
    void test(int second) {
        ProcessorThrowsExceptionIfSecondIsEven processor = new ProcessorThrowsExceptionIfSecondIsEven(() -> second);
        Message message = Mockito.mock(Message.class);
        assertThrows(EvenSecondException.class, () -> processor.process(message));
    }
}
