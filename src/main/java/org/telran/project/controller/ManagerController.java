package org.telran.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telran.project.dto.ManagerDto;
import org.telran.project.entity.Manager;
import org.telran.project.service.ManagerService;
import org.telran.project.service.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("managers")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService service;

    private final Converter<Manager, ManagerDto> converter;

    @GetMapping
    List<ManagerDto> getAll() {
        return service.getAll().stream().map(converter::toDto).collect(Collectors.toList());
    }
}
