package ru.otus.atm.processor;

import java.util.Map;
import ru.otus.atm.model.Banknote;

public interface CountingMachine {

    int countAmountBanknotes(Map<Banknote, Integer> banknotes);

    Map<Banknote, Integer> calcBanknotesForAmount(Map<Banknote, Integer> banknotes, int amount);
}
