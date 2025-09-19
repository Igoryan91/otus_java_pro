package ru.otus.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.otus.dto.PhoneDto;
import ru.otus.models.Phone;

@Component
public class PhoneDtoMapper {

    public Phone mapToEntity(PhoneDto phone) {
        if (phone == null) {
            return null;
        }
        return new Phone(phone.getId(), phone.getNumber());
    }

    public Set<Phone> mapToEntityList(Collection<PhoneDto> phones) {
        if (phones.isEmpty()) {
            return Collections.emptySet();
        }
        return phones.stream().map(this::mapToEntity).collect(Collectors.toSet());
    }

    public PhoneDto mapToDto(Phone phone) {
        if (phone == null) {
            return null;
        }
        return new PhoneDto(phone.getId(), phone.getNumber());
    }

    public Set<PhoneDto> mapToDtoList(Collection<Phone> phones) {
        if (phones.isEmpty()) {
            return Collections.emptySet();
        }
        return phones.stream().map(this::mapToDto).collect(Collectors.toSet());
    }
}
