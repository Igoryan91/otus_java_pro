package ru.otus.atm;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.atm.exception.AmountRequestedException;
import ru.otus.atm.model.Banknote;
import ru.otus.atm.processor.Processor;

@Slf4j
@RequiredArgsConstructor
public class ATM {
    private final Processor processor;

    public int getBalance() {
        return processor.getBalance();
    }

    public void withdraw(int amount) {
        runSafe(() -> processor.withdraw(amount));
    }

    public void replenish(Map<Banknote, Integer> banknotes) {
        runSafe(() -> processor.replenish(banknotes));
    }

    public void replenish(Banknote banknote, int quantity) {
        runSafe(() -> processor.replenish(banknote, quantity));
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
