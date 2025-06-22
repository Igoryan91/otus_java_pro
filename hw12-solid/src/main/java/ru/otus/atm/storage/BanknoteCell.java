package ru.otus.atm.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.otus.atm.model.Banknote;

@Getter
@AllArgsConstructor
public class BanknoteCell {
    private final Banknote banknote;
    private int quantity;

    public BanknoteCell replenish(int count) {
        quantity += count;
        return this;
    }

    public BanknoteCell withdraw(int count) {
        if (count > quantity) {
            throw new IllegalStateException("Not enough banknote");
        }
        quantity -= count;
        return this;
    }
}
