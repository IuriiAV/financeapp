package org.telran.project.service.converter;

import org.springframework.stereotype.Component;
import org.telran.project.dto.ClientDto;
import org.telran.project.dto.ManagerDto;
import org.telran.project.entity.Manager;

import java.util.stream.Collectors;

@Component
public class ManagerDtoConverter implements Converter<Manager, ManagerDto> {

    @Override
    public ManagerDto toDto(Manager manager) {
        return new ManagerDto(manager.getId(),
                manager.getFirstName(),
                manager.getLastName(),
                manager.getClients().stream().map(client -> new ClientDto(client.getId(),
                                client.getFirstName(), client.getLastName(), null))
                        .collect(Collectors.toList()));
    }

    @Override
    public Manager toEntity(ManagerDto managerDto) {
        throw new UnsupportedOperationException();
    }
}
