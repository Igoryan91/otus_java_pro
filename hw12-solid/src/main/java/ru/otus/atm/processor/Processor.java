package ru.otus.atm.processor;

import java.util.Map;
import ru.otus.atm.model.Banknote;

public interface Processor {

    int getBalance();

    int withdrawAmount();

    void withdraw(int amount);

    void replenish(Map<Banknote, Integer> banknotes);

    void replenish(Banknote banknote, int quantity);
}
