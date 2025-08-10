package ru.otus.services;

import java.util.List;
import java.util.Optional;
import ru.otus.dto.ClientDto;
import ru.otus.model.Client;

public interface DBServiceClient {

    ClientDto saveClient(Client client);

    Optional<ClientDto> getClient(long id);

    List<ClientDto> findAll();
}
