package ru.otus.atm.storage;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import ru.otus.atm.exception.AmountRequestedException;
import ru.otus.atm.model.Banknote;

public class StorageImpl implements Storage {

    private final Map<Banknote, BanknoteCell> banknoteCells = new EnumMap<>(Banknote.class);

    private static final int MIN_NOMINAL = 50;

    public StorageImpl() {
        for (Banknote banknote : Banknote.values()) {
            banknoteCells.put(banknote, new BanknoteCell());
        }
    }

    @Override
    public int getBalance() {
        Map<Banknote, Integer> banknotes = getQuantityOfEachBanknotes();
        return countAmountBanknotes(banknotes);
    }

    @Override
    public void replenish(Banknote banknote, int quantity) {
        banknoteCells.get(banknote).replenish(quantity);
    }

    @Override
    public void withdraw(int amount) {
        checkAmountForCorrect(amount);
        checkAmountForEnough(amount);
        Map<Banknote, Integer> banknotes = getQuantityOfEachBanknotes();
        Map<Banknote, Integer> banknotesToWithdraw = calcBanknotesForAmount(banknotes, amount);
        subtractBanknotes(banknotesToWithdraw);
    }

    private Map<Banknote, Integer> getQuantityOfEachBanknotes() {
        return banknoteCells.entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue()
                .getQuantity()));
    }

    private int countAmountBanknotes(Map<Banknote, Integer> banknotes) {
        return banknotes.entrySet().stream().mapToInt(this::amountBanknotes).sum();
    }

    private int amountBanknotes(Map.Entry<Banknote, Integer> banknote) {
        return banknote.getKey().getNominal() * banknote.getValue();
    }

    private void checkAmountForCorrect(int amount) {
        if (amount <= 0 || amount % MIN_NOMINAL != 0) {
            throw new AmountRequestedException("Incorrect amount. Minimum exchange - 50 rub."
                    + "Therefore, the amount should be a multiple of 50.");
        }
    }

    private void checkAmountForEnough(int amount) {
        if (amount > getBalance()) {
            throw new AmountRequestedException("The ATM does not have enough funds to issue such an amount.");
        }
    }

    private Map<Banknote, Integer> calcBanknotesForAmount(Map<Banknote, Integer> banknotes, int amount) {
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

    private int calcBanknoteOneDenominationForAmount(Banknote banknote, int available, int remainAmount) {
        int banknoteCount = remainAmount / banknote.getNominal();
        return Math.min(banknoteCount, available);
    }

    private void checkRemainAmount(int remainAmount) {
        if (remainAmount > 0) {
            throw new AmountRequestedException("Not enough banknotes for the formation of the amount");
        }
    }

    private void subtractBanknotes(Map<Banknote, Integer> banknotes) {
        for (Map.Entry<Banknote, Integer> entry : banknotes.entrySet()) {
            subtract(entry.getKey(), entry.getValue());
        }
    }

    private void subtract(Banknote banknote, int quantity) {
        banknoteCells.get(banknote).withdraw(quantity);
    }
}
