package ru.otus.atm.processor;

import java.util.EnumMap;
import java.util.Map;
import ru.otus.atm.exception.AmountRequestedException;
import ru.otus.atm.model.Banknote;

public class CountingMachineImpl implements CountingMachine {

    @Override
    public int countAmountBanknotes(Map<Banknote, Integer> banknotes) {
        return banknotes.entrySet().stream().mapToInt(this::amountBanknotes).sum();
    }

    @Override
    public Map<Banknote, Integer> calcBanknotesForAmount(Map<Banknote, Integer> banknotes, int amount) {
        Map<Banknote, Integer> banknotesForAmount = new EnumMap<>(Banknote.class);
        int remainAmount = amount;
        for (Banknote banknote : Banknote.values()) {
            if (remainAmount == 0) {
                break;
            }
            int banknoteCount = calcBanknoteOneDenominationForAmount(banknote, banknotes.get(banknote), remainAmount);
            banknotesForAmount.put(banknote, banknoteCount);
            remainAmount -= banknote.getNominal() * banknoteCount;
        }
        checkRemainAmount(remainAmount);
        return banknotesForAmount;
    }

    private int amountBanknotes(Map.Entry<Banknote, Integer> banknote) {
        return banknote.getKey().getNominal() * banknote.getValue();
    }

    private int calcBanknoteOneDenominationForAmount(Banknote banknote, int available, int remainAmount) {
        int banknoteCount = remainAmount / banknote.getNominal();
        return Math.min(banknoteCount, available);
    }

    private void checkRemainAmount(int remainAmount) {
        if (remainAmount > 0) {
            throw new AmountRequestedException("Not enough banknotes for the formation of the amount");
        }
    }
}
