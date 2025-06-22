package ru.otus.atm.processor;

import static ru.otus.atm.model.Banknote.FIFTY;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import ru.otus.atm.exception.AmountRequestedException;
import ru.otus.atm.model.Banknote;
import ru.otus.atm.storage.Storage;

@RequiredArgsConstructor
public class ProcessorImpl implements Processor {

    private final Storage storage;

    private final CountingMachine countingMachine;

    @Override
    public int getBalance() {
        Map<Banknote, Integer> banknotes = storage.getQuantityOfEachBanknotes();
        return countingMachine.countAmountBanknotes(banknotes);
    }

    @Override
    public int withdrawAmount() {
        return 0;
    }

    @Override
    public void withdraw(int amount) {
        checkAmountForCorrect(amount);
        checkAmountForEnough(amount);
        Map<Banknote, Integer> banknotes = storage.getQuantityOfEachBanknotes();
        Map<Banknote, Integer> banknotesToWithdraw = countingMachine.calcBanknotesForAmount(banknotes, amount);
        subtractBanknotes(banknotesToWithdraw);
    }

    @Override
    public void replenish(Map<Banknote, Integer> banknotes) {
        storage.replenish(banknotes);
    }

    @Override
    public void replenish(Banknote banknote, int quantity) {
        storage.replenish(banknote, quantity);
    }

    private void checkAmountForCorrect(int amount) {
        if (amount <= 0 || amount % FIFTY.getNominal() != 0) {
            throw new AmountRequestedException("Incorrect amount. Minimum exchange - 50 rub."
                    + "Therefore, the amount should be a multiple of 50.");
        }
    }

    private void checkAmountForEnough(int amount) {
        if (amount > getBalance()) {
            throw new AmountRequestedException("The ATM does not have enough funds to issue such an amount.");
        }
    }

    private void subtractBanknotes(Map<Banknote, Integer> banknotes) {
        for (Map.Entry<Banknote, Integer> entry : banknotes.entrySet()) {
            storage.withdraw(entry.getKey(), entry.getValue());
        }
    }
}
