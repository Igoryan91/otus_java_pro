package ru.otus.atm;

import java.util.Map;
import ru.otus.atm.model.Banknote;

public interface ATM {

    int getBalance();

    void withdraw(int amount);

    void replenish(Map<Banknote, Integer> banknotes);

    void replenish(Banknote banknote, int quantity);
}
