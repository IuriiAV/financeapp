package org.telran.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/{id}")
    ResponseEntity<ClientDto> getById(@PathVariable("id") String id) {
        return new ResponseEntity<>(converter.toDto(service.getById(id)), HttpStatus.OK);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ClientDto save(@RequestBody ClientDto clientDto) {
        return converter.toDto(service.create(converter.toEntity(clientDto)));
    }
}