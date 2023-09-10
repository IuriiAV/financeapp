package org.telran.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.telran.project.BadArgumentsException;
import org.telran.project.dto.ClientDto;
import org.telran.project.entity.Client;
import org.telran.project.service.ClientService;
import org.telran.project.service.converter.Converter;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;


@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @MockBean
    private ClientService clientService;

    @MockBean
    private Converter<Client, ClientDto> converter;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAll() throws Exception {
        Client client = new Client(UUID.randomUUID(), "Alex", "Alexeev");
        //Говорим сервису возвращать список объектов Client при вызове метода getAll
        Mockito.when(clientService.getAll()).thenReturn(List.of(client));
        //Конвертируем клиента в dto
        Mockito.when(converter.toDto(client)).thenReturn(new ClientDto(client.getId(), client.getFirstName(),
                client.getLastName(), null));
        //Вызываем метод контроллера по адресу /clients
        mockMvc.perform(MockMvcRequestBuilders.get("/clients").contentType(MediaType.APPLICATION_JSON))
                //Выводим запросы и ответы в консоль
                .andDo(MockMvcResultHandlers.print())
                //Ожидаем что такой метод вернет код ответа 200
                .andExpect(MockMvcResultMatchers.status().isOk())
                //через метод content() можем получить тело ответа в json
                //конвертируем список ожидаемый список в json строку и сравним с тем что
                //отдано в теле ответа
                //Можно сравнивать по поиску значений отдельных полей в теле ответа
                //будет показано в тесте создания клиента
                .andExpect(MockMvcResultMatchers.content().json(asJsonString(List.of(new ClientDto(
                        client.getId(), client.getFirstName(), client.getLastName(), null)))));
    }

    @Test
    void getByIdWhenIdIsExists() throws Exception {
        Client existsClient = new Client(UUID.randomUUID(), "Oleg", "Olegov");
        ClientDto existClientDto = new ClientDto(existsClient.getId(), existsClient.getFirstName(),
                existsClient.getLastName(), null);
        Mockito.when(clientService.getById(existsClient.getId().toString())).thenReturn(existsClient);
        Mockito.when(converter.toDto(existsClient)).thenReturn(existClientDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/clients/" + existsClient.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                //Выводим запросы и ответы в консоль
                .andDo(MockMvcResultHandlers.print())
                //Ожидаем что такой метод вернет код ответа 200
                .andExpect(MockMvcResultMatchers.status().isOk())
                //Сравниваем json ответа и ожидаемый объект конвертированный в json
                //лучше использовать jsonPath - показанный в методе createClient
                .andExpect(MockMvcResultMatchers.content().json(asJsonString(existClientDto)));
    }

    @Test
    void getByIdWhenIdIsNotExists() throws Exception {
        Client notExistsClient = new Client(UUID.randomUUID(), "Oleg", "Olegov");
        //Говорим сервису вызвать исключение, так как этого объекта нет
        Mockito.when(clientService.getById(notExistsClient.getId().toString())).thenThrow(BadArgumentsException.class);
        //Вызываем метод метод по адресу /clients/{id}, где вместо id подставляем параметр, ид клиента
        mockMvc.perform(MockMvcRequestBuilders.get("/clients/{id}", notExistsClient.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                //Выводим запросы и ответы в консоль
                .andDo(MockMvcResultHandlers.print())
                //Ожидаем, что когда не найден клиент, то будет данный код ответа
                //Код ответа прописали в созданном классе исключении BadArgumentsException
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                //Ожидаем что данный запрос выбросит исключение следующего вида
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadArgumentsException));
    }

    @Test
    void createClient() throws Exception {
        //Объект, входящий в метод контроллера
        ClientDto newClientDto = new ClientDto(null, "Oleg", "Olegov", null);
        //Объект, конвертированный из dto в сущность, до сохранения в системе
        Client newClientWithoutId = new Client(null, newClientDto.getFirstName(), newClientDto.getFirstName());
        //Объект, который "сохранился" в системе и вернулся из mock сервиса
        Client newClient = new Client(UUID.randomUUID(), newClientWithoutId.getFirstName(), newClientWithoutId.getLastName());
        // Говорим конвертеру принять дто и вернуть объект до сохранения в системе
        Mockito.when(converter.toEntity(newClientDto)).thenReturn(newClientWithoutId);
        // Говорим сервису принять объект до сохранения в системе и вернуть объект из системы
        Mockito.when(clientService.create(newClientWithoutId)).thenReturn(newClient);
        // Говорим конвертеру принять объект из системы и вернуть дто
        Mockito.when(converter.toDto(newClient)).thenReturn(new ClientDto(
                newClient.getId(), newClient.getFirstName(), newClient.getLastName(), null));

        //Смысл теста в том, что бы подать на вход post метода по адресу clients объект DTO и
        //проверить что вернулся объект с id
        mockMvc.perform(MockMvcRequestBuilders
                        //используем метод post и указываем адрес
                        .post("/clients")
                        //Передаем в теле json, с помощью вспомогательного метода asJsonString
                        //преобразуем наш dto в строку вида json
                        .content(asJsonString(newClientDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //выводим результат в лог
                .andDo(MockMvcResultHandlers.print())
                //Ожидаем что метод вернет код 201 - правила REST , если создаем объект, то возвращаем 201 код
                .andExpect(MockMvcResultMatchers.status().isCreated())
                //Следующие две строки говорят о том, что ищем в теле ответа (там json нашего объекта, что
                //возвращает метод) параметры, которые обозначаем как "$.имя_параметра", и сравниваем что
                //за значение там ожидаем
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(newClient.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Is.is("Oleg")));
    }

    //Метод преобразования объекта в строку вида json
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}