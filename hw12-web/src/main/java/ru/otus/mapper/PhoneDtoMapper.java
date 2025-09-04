package ru.otus.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import ru.otus.dto.PhoneDto;
import ru.otus.model.Phone;

@RequiredArgsConstructor
public class PhoneDtoMapper {

    public PhoneDto mapToDto(Phone phone) {
        return new PhoneDto(phone.getId(), phone.getNumber());
    }

    public List<PhoneDto> mapToDto(List<Phone> phones) {
        return phones.stream().map(this::mapToDto).toList();
    }
}
