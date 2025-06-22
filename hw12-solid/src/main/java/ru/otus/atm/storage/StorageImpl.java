package ru.otus.atm.storage;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import ru.otus.atm.model.Banknote;

public class StorageImpl implements Storage {

    private final Map<Banknote, BanknoteCell> banknoteCells = new EnumMap<>(Banknote.class);

    public StorageImpl() {
        for (Banknote banknote : Banknote.values()) {
            banknoteCells.put(banknote, new BanknoteCell(banknote, 0));
        }
    }

    @Override
    public void replenish(Map<Banknote, Integer> banknoteCells) {
        banknoteCells.forEach(this::replenish);
    }

    @Override
    public void replenish(Banknote banknote, int quantity) {
        banknoteCells.get(banknote).replenish(quantity);
    }

    @Override
    public int getQuantityBanknote(Banknote banknote) {
        return banknoteCells.get(banknote).getQuantity();
    }

    @Override
    public Map<Banknote, Integer> getQuantityOfEachBanknotes() {
        return banknoteCells.entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue()
                .getQuantity()));
    }

    @Override
    public void withdraw(Banknote banknote, int quantity) {
        banknoteCells.get(banknote).withdraw(quantity);
    }
}
