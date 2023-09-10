Вариант тестирования контроллеров:

Использование аннотации **@WebMvcTest**.
При проставлении данной аннотации над классом, в котором собраны тесты определенного контроллера,
spring не будет загружать весь контекст, компоненты, отмеченные @Controller, @Service, @Repository не будут загружены.
В контекст попадут только bean слоя web @Controller, @ControllerAdvice, @JsonComponent и т.д, так же будет настроен Security

Необходимо создать класс с тестами для выбранного контроллера и аннотировать его @WebMvcTest, в параметрах аннотации
нужно указать класс контроллер для которого выполняем тестирование.

В качестве примера будем тестировать класс ClientController этого приложения, создает класс ClientControllerTest,
аннотируем его  @WebMvcTest(ClientController.class).

    @WebMvcTest(ClientController.class)
    class ClientControllerTest {

Класс ClientController зависит от двух других классов: ClientService, Converter сделаем их моками, для этого пропишем в
ClientControllerTest переменные этих типов с аннотацией @MockBean.

Вызовы методов нашего контроллера и сверку ответов, будет производить класс MockMvc, заинжектим его в тест, код
нашего класса:

    @WebMvcTest(ClientController.class)
    class ClientControllerTest {

        @MockBean
        private ClientService clientService;
    
        @MockBean
        private Converter converter;
    
        @Autowired
        private MockMvc mockMvc;
    }

Реализуем тест метода getAll.
Тестовый запрос формируется в методе **perform** у объекта типа **mockMvc**, в него передаем конструктор запроса:

    MockMvcRequestBuilders.get("/clients").contentType(MediaType.APPLICATION_JSON)

Указываем тип метода get, post, put, delete, в параметрах адрес, по которому обращаться,тип принимаемого контента и параметры, 
если необходимы.

Далее, желательно указать команду вывода, у объекта типа **mockMvc**, сформированного тестового запроса в логи,

    MockMvcResultHandlers.print()  или MockMvcResultHandlers.log()

Далее, у объекта типа **mockMvc** прописываем, то, что ожидаем получить в ответ от этого метода, например, код статуса ответа.
Код метода:

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/clients").contentType(MediaType.APPLICATION_JSON))
               .andDo(MockMvcResultHandlers.print())
               .andExpect(MockMvcResultMatchers.status().isOk());
    }

Данный метод протестирует, что есть метод по определенному адресу и он возвращает код ответа 200.

Но лучше, дополнить такие методы тестовыми данными через Mockito и проверять также возвращаемые значения.

Примеры с описаниями в классе **ClientControllerTest**.

**Важно! Про коды ответа.**

Обратите внимание, что контроллеры могу отдавать различные коды ответа, успех - код 200, создание объекта код - 201,
и другие.

Если все методы в любых ситуациях отдают 200 код, это очень плохо.

Коды ответа можно проставить над исключениями, которые могут быть выброшены контроллером. 
Или над методом контроллера, как пример над методом создания клиента в классе ClientController
Или можно возвращать из методов контроллера тип ResponseEntity<ТипНашегоОбъекта> с кодом ответа, а в методе 
писать следующее return new ResponseEntity<>(НАШ объект, HttpStatus.OK), пример метода:

    @GetMapping("/{id}")
    ResponseEntity<ClientDto> getById(@PathVariable("id") String id) {
        return new ResponseEntity<>(converter.toDto(service.getById(id)), HttpStatus.OK);
    }