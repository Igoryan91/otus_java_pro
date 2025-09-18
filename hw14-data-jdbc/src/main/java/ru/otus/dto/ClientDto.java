package ru.otus.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientDto {

    private long id;

    private String name;

    private AddressDto address;

    private List<PhoneDto> phones = new ArrayList<>();

    public ClientDto() {
        phones = new ArrayList<>();
    }

    public void setPhones(Set<PhoneDto> phones) {
        if (phones != null) {
            this.phones.clear();
            this.phones.addAll(phones);
        }
    }
}
