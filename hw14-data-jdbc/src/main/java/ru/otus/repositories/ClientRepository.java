package ru.otus.repositories;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.models.Client;

public interface ClientRepository extends ListCrudRepository<Client, Long> {}
