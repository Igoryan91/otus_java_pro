package ru.otus.atm.storage;

import java.util.Map;
import ru.otus.atm.model.Banknote;

public interface Storage {

    void replenish(Map<Banknote, Integer> banknoteCells);

    void replenish(Banknote banknote, int quantity);

    int getQuantityBanknote(Banknote banknote);

    Map<Banknote, Integer> getQuantityOfEachBanknotes();

    void withdraw(Banknote banknote, int quantity);
}
