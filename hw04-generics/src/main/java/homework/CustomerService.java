package homework;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class CustomerService {
    private final TreeMap<Customer, String> customerTreeMap =
            new TreeMap<>(Comparator.comparingLong(Customer::getScores));

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry<Customer, String> result = customerTreeMap.firstEntry();

        return result != null ? Map.entry(new Customer(result.getKey()), result.getValue()) : null;
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry<Customer, String> result = customerTreeMap.higherEntry(customer);

        return result != null ? Map.entry(new Customer(result.getKey()), result.getValue()) : null;
    }

    public void add(Customer customer, String data) {
        customerTreeMap.put(customer, data);
    }
}
