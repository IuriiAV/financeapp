package org.telran.project.service.converter;

public interface Converter<ENTITY, DTO> {

    DTO toDto(ENTITY entity);

    ENTITY toEntity(DTO dto);
}