package org.telran.project.service;

import org.telran.project.entity.Client;

import java.util.List;

public interface ClientService {

    List<Client> getAll();

    Client getById(String id);

    Client create(Client client);
}