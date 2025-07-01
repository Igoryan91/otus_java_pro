package ru.otus.atm.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BanknoteCell {
    private int quantity;

    public void replenish(int count) {
        quantity += count;
    }

    public BanknoteCell withdraw(int count) {
        if (count > quantity) {
            throw new IllegalStateException("Not enough banknote");
        }
        quantity -= count;
        return this;
    }
}
