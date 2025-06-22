package ru.otus.atm;

import static ru.otus.atm.model.Banknote.*;

import java.util.EnumMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import ru.otus.atm.model.Banknote;
import ru.otus.atm.processor.CountingMachineImpl;
import ru.otus.atm.processor.Processor;
import ru.otus.atm.processor.ProcessorImpl;
import ru.otus.atm.storage.Storage;
import ru.otus.atm.storage.StorageImpl;

@Slf4j
public class Main {

    private static final String MESSAGE_BALANCE = "Total amount at the ATM: {}";

    public static void main(String[] args) {

        Map<Banknote, Integer> packBanknotes = new EnumMap<>(Banknote.class);
        packBanknotes.put(FIFTY, 1000);
        packBanknotes.put(HUNDRED, 1000);
        packBanknotes.put(TWO_HUNDRED, 1000);
        packBanknotes.put(FIVE_HUNDRED, 1000);
        packBanknotes.put(THOUSAND, 1000);
        packBanknotes.put(TWO_THOUSAND, 1000);
        packBanknotes.put(FIVE_THOUSAND, 1000);

        Storage storage = new StorageImpl();
        Processor processor = new ProcessorImpl(storage, new CountingMachineImpl());
        storage.replenish(packBanknotes);
        ATM atm = new ATM(processor);

        log.info(MESSAGE_BALANCE, (atm.getBalance()));

        atm.replenish(FIVE_THOUSAND, 1000);
        atm.replenish(THOUSAND, 1000);
        atm.replenish(HUNDRED, 1000);
        atm.replenish(FIFTY, 1000);

        log.info(MESSAGE_BALANCE, (atm.getBalance()));

        Map<Banknote, Integer> packBanknotes2 = new EnumMap<>(Banknote.class);
        packBanknotes2.put(FIFTY, 2000);
        packBanknotes2.put(HUNDRED, 2000);
        packBanknotes2.put(TWO_HUNDRED, 1000);
        packBanknotes2.put(FIVE_HUNDRED, 1000);
        packBanknotes2.put(THOUSAND, 2000);
        packBanknotes2.put(TWO_THOUSAND, 1000);
        packBanknotes2.put(FIVE_THOUSAND, 2000);

        atm.replenish(packBanknotes2);

        log.info(MESSAGE_BALANCE, (atm.getBalance()));
        atm.withdraw(15);

        atm.withdraw(15000000);
        atm.withdraw(15000000);

        log.info(MESSAGE_BALANCE, (atm.getBalance()));

        atm.withdraw(150);
    }
}
