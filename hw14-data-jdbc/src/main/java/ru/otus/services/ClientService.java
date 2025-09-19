package ru.otus.services;

import java.util.List;
import ru.otus.dto.ClientDto;

public interface ClientService {

    List<ClientDto> findAll();

    ClientDto save(ClientDto book);
}
