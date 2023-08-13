package org.telran.project.service.converter;

import org.springframework.stereotype.Component;
import org.telran.project.dto.ClientDto;
import org.telran.project.dto.ManagerDto;
import org.telran.project.entity.Client;

@Component
public class ClientDtoConverter implements Converter<Client, ClientDto> {

    @Override
    public ClientDto toDto(Client client) {
        return new ClientDto(client.getId(),
                client.getFirstName()
                , client.getLastName(),
                new ManagerDto(client.getManager().getId(),
                        client.getManager().getFirstName(),
                        client.getManager().getLastName(), null));
    }

    @Override
    public Client toEntity(ClientDto clientDto) {
        throw new UnsupportedOperationException();
    }
}