package org.telran.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telran.project.entity.Manager;
import org.telran.project.repository.ManagerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository repository;

    @Override
    public List<Manager> getAll() {
        return repository.findAll();
    }
}