package ru.otus.atm.storage;

import ru.otus.atm.model.Banknote;

public interface Storage {

    int getBalance();

    void replenish(Banknote banknote, int quantity);

    void withdraw(int amount);
}
