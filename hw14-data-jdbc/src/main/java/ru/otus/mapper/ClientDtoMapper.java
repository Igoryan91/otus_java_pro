package ru.otus.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.dto.ClientDto;
import ru.otus.models.Client;

@Component
@RequiredArgsConstructor
public class ClientDtoMapper {

    private final PhoneDtoMapper phoneDtoMapper;

    private final AddressDtoMapper addressDtoMapper;

    public Client mapToEntity(ClientDto client) {
        if (client == null) {
            return null;
        }
        return new Client(
                client.getId(),
                client.getName(),
                addressDtoMapper.mapToEntity(client.getAddress()),
                phoneDtoMapper.mapToEntityList(client.getPhones()));
    }

    public ClientDto mapToDto(Client client) {
        if (client == null) {
            return null;
        }
        return new ClientDto(
                client.getId(),
                client.getName(),
                addressDtoMapper.mapToDto(client.getAddress()),
                new ArrayList<>(phoneDtoMapper.mapToDtoList(client.getPhones())));
    }

    public List<ClientDto> mapToDtoList(List<Client> clients) {
        if (clients.isEmpty()) {
            return Collections.emptyList();
        }
        return clients.stream().map(this::mapToDto).toList();
    }
}
