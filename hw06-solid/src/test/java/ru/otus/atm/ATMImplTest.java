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
import ru.otus.atm.storage.Storage;
import ru.otus.atm.storage.StorageImpl;

class ATMImplTest {
    private ATMImpl atmimpl;
    private Storage storage;

    @BeforeEach
    void setUp() {
        this.storage = Mockito.spy(new StorageImpl());
        atmimpl = new ATMImpl(this.storage);
    }

    @Test
    @DisplayName("Проверка внесения суммы разными купюрами")
    void shouldCorrectlyAcceptAmountByDifferentBanknotes() {
        Map<Banknote, Integer> packBanknotes = getPackBanknotes();
        atmimpl.replenish(packBanknotes);
        int sumBanknotes = calcSumBanknotes(packBanknotes);
        assertEquals(sumBanknotes, atmimpl.getBalance());
    }

    @Test
    @DisplayName("Проверка внесения суммы купюрами одного номинала")
    void shouldCorrectlyAcceptAmountByBanknotesOneNominal() {
        atmimpl.replenish(FIVE_THOUSAND, 1000);
        int sumFiveThousand = FIVE_THOUSAND.getNominal() * 1000;
        assertEquals(sumFiveThousand, atmimpl.getBalance());

        atmimpl.replenish(TWO_HUNDRED, 1000);
        int sumTwoHundred = TWO_HUNDRED.getNominal() * 1000;
        assertEquals(sumFiveThousand + sumTwoHundred, atmimpl.getBalance());

        atmimpl.replenish(FIFTY, 459000);
        int sumFifty = FIFTY.getNominal() * 459000;
        assertEquals(sumFiveThousand + sumTwoHundred + sumFifty, atmimpl.getBalance());
    }

    @Test
    @DisplayName("Проверка снятия суммы")
    void shouldCorrectlyWithdrawAmount() {
        Map<Banknote, Integer> packBanknotes = getPackBanknotes();
        int banknotesSum = calcSumBanknotes(packBanknotes);
        atmimpl.replenish(packBanknotes);

        int withdrawAmount1 = 1000000;
        atmimpl.withdraw(withdrawAmount1);
        banknotesSum -= withdrawAmount1;
        assertEquals(banknotesSum, atmimpl.getBalance());

        int withdrawAmount2 = 450750;
        atmimpl.withdraw(withdrawAmount2);
        banknotesSum -= withdrawAmount2;
        assertEquals(banknotesSum, atmimpl.getBalance());

        int withdrawAmount3 = 350;
        atmimpl.withdraw(withdrawAmount3);
        banknotesSum -= withdrawAmount3;
        assertEquals(banknotesSum, atmimpl.getBalance());
    }

    @Test
    @DisplayName("Проверка ошибки снятия при недостатке средств")
    void shouldNotWithdrawAmountIfNotEnoughMoney() {
        atmimpl.replenish(FIFTY, 1000);
        int banknotesSum = FIFTY.getNominal() * 1000;
        int withdrawAmount = banknotesSum + 1;
        atmimpl.withdraw(withdrawAmount);
        assertThrows(AmountRequestedException.class, () -> storage.withdraw(withdrawAmount));
    }

    @Test
    @DisplayName("Проверка ошибки снятия при недостатке каких-то банкнот")
    void shouldNotWithdrawAmountIfNotEnoughBanknotes() {
        atmimpl.replenish(FIFTY, 10);
        atmimpl.replenish(THOUSAND, 10);

        int sumFifty = FIFTY.getNominal() * 10;
        atmimpl.withdraw(sumFifty);

        assertTrue(atmimpl.getBalance() > sumFifty);
        assertThrows(AmountRequestedException.class, () -> storage.withdraw(sumFifty));
    }

    @Test
    @DisplayName("Проверка запрета ввода неверной суммы")
    void shouldThrowErrorWhenWithdrawAmountWithIncorrectValue() {
        atmimpl.withdraw(FIFTY.getNominal() - 1);
        assertThrows(AmountRequestedException.class, () -> storage.withdraw(1));

        atmimpl.withdraw(-1);
        assertThrows(AmountRequestedException.class, () -> storage.withdraw(-1));
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
        return banknotes.entrySet().stream()
                .mapToInt(entry -> entry.getKey().getNominal() * entry.getValue())
                .sum();
    }
}
