package org.telran.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telran.project.entity.Client;
import org.telran.project.repository.ClientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;

    @Override
    public List<Client> getAll() {
        List<Client> all = repository.findAll();
        System.out.println(all);
        return all;
    }
}