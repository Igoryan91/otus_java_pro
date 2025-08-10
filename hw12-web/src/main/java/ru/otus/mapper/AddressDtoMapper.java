package ru.otus.mapper;

import lombok.RequiredArgsConstructor;
import ru.otus.dto.AddressDto;
import ru.otus.model.Address;

@RequiredArgsConstructor
public class AddressDtoMapper {

    public AddressDto mapToDto(Address adress) {
        return new AddressDto(adress.getId(), adress.getStreet());
    }
}
