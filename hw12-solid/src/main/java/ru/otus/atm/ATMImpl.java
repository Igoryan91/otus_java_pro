package ru.otus.atm;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.atm.exception.AmountRequestedException;
import ru.otus.atm.model.Banknote;
import ru.otus.atm.storage.Storage;

@Slf4j
@RequiredArgsConstructor
public class ATMImpl implements ATM {
    private final Storage storage;

    @Override
    public int getBalance() {
        return storage.getBalance();
    }

    @Override
    public void withdraw(int amount) {
        runSafe(() -> storage.withdraw(amount));
    }

    @Override
    public void replenish(Map<Banknote, Integer> banknotes) {
        runSafe(() -> banknotes.forEach(this::replenish));
    }

    @Override
    public void replenish(Banknote banknote, int quantity) {
        runSafe(() -> storage.replenish(banknote, quantity));
    }

    private void runSafe(Runnable runnable) {
        try {
            runnable.run();
            log.info("Operation successful");
        } catch (AmountRequestedException e) {
            log.warn(e.getMessage());
        } catch (Exception e) {
            log.error("An unforeseen mistake has occurred, try again", e);
        }
    }
}
