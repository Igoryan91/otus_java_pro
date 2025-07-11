package ru.otus.atm.exception;

public class AmountRequestedException extends RuntimeException {
    public AmountRequestedException(String message) {
        super(message);
    }
}
