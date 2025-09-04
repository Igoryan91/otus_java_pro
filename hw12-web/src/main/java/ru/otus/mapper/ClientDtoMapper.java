package ru.otus.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import ru.otus.dto.ClientDto;
import ru.otus.model.Client;

@RequiredArgsConstructor
public class ClientDtoMapper {
    private final AddressDtoMapper addressDtoMapper;
    private final PhoneDtoMapper phoneDtoMapper;

    public ClientDto mapToDto(Client client) {
        return new ClientDto(
                client.getId(),
                client.getName(),
                addressDtoMapper.mapToDto(client.getAddress()),
                phoneDtoMapper.mapToDto(client.getPhones()));
    }

    public List<ClientDto> mapToDto(List<Client> clients) {
        return clients.stream().map(this::mapToDto).toList();
    }
}
