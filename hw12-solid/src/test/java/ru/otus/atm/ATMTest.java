package ru.otus.atm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.otus.atm.model.Banknote.FIFTY;
import static ru.otus.atm.model.Banknote.FIVE_HUNDRED;
import static ru.otus.atm.model.Banknote.FIVE_THOUSAND;
import static ru.otus.atm.model.Banknote.HUNDRED;
import static ru.otus.atm.model.Banknote.THOUSAND;
import static ru.otus.atm.model.Banknote.TWO_HUNDRED;
import static ru.otus.atm.model.Banknote.TWO_THOUSAND;

import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.atm.exception.AmountRequestedException;
import ru.otus.atm.model.Banknote;
import ru.otus.atm.processor.CountingMachineImpl;
import ru.otus.atm.processor.Processor;
import ru.otus.atm.processor.ProcessorImpl;
import ru.otus.atm.storage.Storage;
import ru.otus.atm.storage.StorageImpl;

class ATMTest {
    private ATM atm;
    private Processor processor;

    @BeforeEach
    void setUp() {
        Storage storage = new StorageImpl();
        processor = Mockito.spy(new ProcessorImpl(storage, new CountingMachineImpl()));
        atm = new ATM(processor);
    }

    @Test
    @DisplayName("Проверка внесения суммы разными купюрами")
    void shouldCorrectlyAcceptAmountByDifferentBanknotes() {
        Map<Banknote, Integer> packBanknotes = getPackBanknotes();
        atm.replenish(packBanknotes);
        int sumBanknotes = calcSumBanknotes(packBanknotes);
        assertEquals(sumBanknotes, atm.getBalance());
    }

    @Test
    @DisplayName("Проверка внесения суммы купюрами одного номинала")
    void shouldCorrectlyAcceptAmountByBanknotesOneNominal() {
        atm.replenish(FIVE_THOUSAND, 1000);
        int sumFiveThousand = FIVE_THOUSAND.getNominal() * 1000;
        assertEquals(sumFiveThousand, atm.getBalance());

        atm.replenish(TWO_HUNDRED, 1000);
        int sumTwoHundred = TWO_HUNDRED.getNominal() * 1000;
        assertEquals(sumFiveThousand + sumTwoHundred, atm.getBalance());

        atm.replenish(FIFTY, 459000);
        int sumFifty = FIFTY.getNominal() * 459000;
        assertEquals(sumFiveThousand + sumTwoHundred + sumFifty, atm.getBalance());
    }

    @Test
    @DisplayName("Проверка снятия суммы")
    void shouldCorrectlyWithdrawAmount() {
        Map<Banknote, Integer> packBanknotes = getPackBanknotes();
        int banknotesSum = calcSumBanknotes(packBanknotes);
        atm.replenish(packBanknotes);

        int withdrawAmount1 = 1000000;
        atm.withdraw(withdrawAmount1);
        banknotesSum -= withdrawAmount1;
        assertEquals(banknotesSum, atm.getBalance());

        int withdrawAmount2 = 450750;
        atm.withdraw(withdrawAmount2);
        banknotesSum -= withdrawAmount2;
        assertEquals(banknotesSum, atm.getBalance());

        int withdrawAmount3 = 350;
        atm.withdraw(withdrawAmount3);
        banknotesSum -= withdrawAmount3;
        assertEquals(banknotesSum, atm.getBalance());
    }

    @Test
    @DisplayName("Проверка ошибки снятия при недостатке средств")
    void shouldNotWithdrawAmountIfNotEnoughMoney() {
        atm.replenish(FIFTY, 1000);
        int banknotesSum = FIFTY.getNominal() * 1000;
        int withdrawAmount = banknotesSum + 1;
        atm.withdraw(withdrawAmount);
        assertThrows(AmountRequestedException.class, () -> processor.withdraw(withdrawAmount));
    }

    @Test
    @DisplayName("Проверка ошибки снятия при недостатке каких-то банкнот")
    void shouldNotWithdrawAmountIfNotEnoughBanknotes() {
        atm.replenish(FIFTY, 10);
        atm.replenish(THOUSAND, 10);

        int sumFifty = FIFTY.getNominal() * 10;
        atm.withdraw(sumFifty);

        assertTrue(atm.getBalance() > sumFifty);
        assertThrows(AmountRequestedException.class, () -> processor.withdraw(sumFifty));
    }

    @Test
    @DisplayName("Проверка запрета ввода неверной суммы")
    void shouldThrowErrorWhenWithdrawAmountWithIncorrectValue() {
        atm.withdraw(FIFTY.getNominal() - 1);
        assertThrows(AmountRequestedException.class, () -> processor.withdraw(1));

        atm.withdraw(-1);
        assertThrows(AmountRequestedException.class, () -> processor.withdraw(-1));
    }

    private Map<Banknote, Integer> getPackBanknotes() {
        Map<Banknote, Integer> packBanknotes = new EnumMap<>(Banknote.class);
        packBanknotes.put(FIFTY, 1000);
        packBanknotes.put(HUNDRED, 1000);
        packBanknotes.put(TWO_HUNDRED, 1000);
        packBanknotes.put(FIVE_HUNDRED, 1000);
        packBanknotes.put(THOUSAND, 1000);
        packBanknotes.put(TWO_THOUSAND, 1000);
        packBanknotes.put(FIVE_THOUSAND, 1000);
        return packBanknotes;
    }

    private int calcSumBanknotes(Map<Banknote, Integer> banknotes) {
        return banknotes.entrySet()
                        .stream()
                        .mapToInt(entry -> entry.getKey().getNominal() * entry.getValue())
                        .sum();
    }
}
