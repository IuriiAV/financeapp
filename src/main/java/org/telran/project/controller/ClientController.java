package org.telran.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telran.project.dto.ClientDto;
import org.telran.project.entity.Client;
import org.telran.project.service.ClientService;
import org.telran.project.service.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    private final Converter<Client, ClientDto> converter;

    @GetMapping
    List<ClientDto> getAll() {
        return service.getAll().stream().map(converter::toDto).collect(Collectors.toList());
    }
}