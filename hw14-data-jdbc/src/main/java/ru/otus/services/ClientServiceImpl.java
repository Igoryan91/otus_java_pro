package ru.otus.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dto.ClientDto;
import ru.otus.mapper.ClientDtoMapper;
import ru.otus.repositories.ClientRepository;

@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    private final ClientDtoMapper clientDtoMapper;

    @Override
    public List<ClientDto> findAll() {
        return clientDtoMapper.mapToDtoList(clientRepository.findAll());
    }

    @Override
    @Transactional
    public ClientDto save(ClientDto client) {
        return clientDtoMapper.mapToDto(clientRepository.save(clientDtoMapper.mapToEntity(client)));
    }
}
