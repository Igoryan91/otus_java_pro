package ru.otus.mapper;

import org.springframework.stereotype.Component;
import ru.otus.dto.AddressDto;
import ru.otus.models.Address;

@Component
public class AddressDtoMapper {

    public Address mapToEntity(AddressDto address) {
        if (address == null) {
            return null;
        }
        return new Address(address.getId(), address.getStreet());
    }

    public AddressDto mapToDto(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressDto(address.getId(), address.getStreet());
    }
}
