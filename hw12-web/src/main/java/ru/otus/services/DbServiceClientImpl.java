package ru.otus.services;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.dto.ClientDto;
import ru.otus.mapper.ClientDtoMapper;
import ru.otus.model.Client;
import ru.otus.repository.DataTemplate;
import ru.otus.sessionmanager.TransactionManager;

@RequiredArgsConstructor
public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final TransactionManager transactionManager;
    private final DataTemplate<Client> clientDataTemplate;
    private final ClientDtoMapper clientDtoMapper;

    @Override
    public ClientDto saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();
            if (client.getId() == null) {
                var savedClient = clientDataTemplate.insert(session, clientCloned);
                log.info("created client: {}", clientCloned);
                return clientDtoMapper.mapToDto(savedClient.clone());
            }
            var savedClient = clientDataTemplate.update(session, clientCloned);
            log.info("updated client: {}", savedClient);
            return clientDtoMapper.mapToDto(savedClient);
        });
    }

    @Override
    public Optional<ClientDto> getClient(long id) {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientOptional = clientDataTemplate.findById(session, id);
            log.info("client: {}", clientOptional);
            return clientOptional.map(clientDtoMapper::mapToDto);
        });
    }

    @Override
    public List<ClientDto> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findAll(session);
            log.info("clientList:{}", clientList);
            return clientDtoMapper.mapToDto(clientList);
        });
    }
}
