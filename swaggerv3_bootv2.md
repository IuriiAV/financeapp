**Добавление Swagger (OpenAPI) в проект :**

Проект построен на spring boot версии 2(для версии spring boot 3, немного другие зависимости).

Добавлять будем swagger (теперь он OpenAPI) версии 3

В pom.xml достаточно добавить две зависимости:

    <dependency>
        <groupId>io.swagger.core.v3</groupId>
        <artifactId>swagger-annotations</artifactId>
        <version>2.2.8</version>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-ui</artifactId>
        <version>1.6.14</version>
    </dependency>

Все, swagger подключен и работает. 
Так как в проекте используется spring security, необходимо оставить адреса для доступа к документации
свободными.

Так как у нас UI swagger версии 3, то у него всего три ресурса которые нужно добавить в конфигурацию
security (у swagger UI версии 2 - ресурсов больше) :
добавляем вот такую строку, туда, где перечисляем ресурсы для открытого доступа в файле конфигурации spring
security:
    
    .antMatchers( "/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()

Все, никаких дополнительных настроек для swagger можно не делать, он подключен и доступен.

Интерфейс swagger находится по адресу:
http://localhost:8080/swagger-ui.html или http://localhost:8080/swagger-ui/index.html

Заходим и видим, что swagger автоматически собрал все наши rest api и вывел их на единой странице, где
можно попробовать как работает каждый rest метод, посмотреть какие параметры требуются для каждого метода
и посмотреть какой формат передаваемой и возвращаемой структуры данных.

**Добавление описания данных методов для более полезного отображения их.**

Так же, можно для более красивой визуализации нашей апи документации, добавить класс конфигурации swagger
в проект, что бы настроить общий заголовок документации, добавим класс и аннотацию над ним:

    @OpenAPIDefinition(
        info = @Info(
                title = "Bank application api",
                description = "Bank application", version = "1.0.0",
                contact = @Contact(
                        name = "Alex Alexov",
                        email = "alex@alexov.com",
                        url = "http://alexov.com"
                )
        )
    )
    public class OpenApiConfig {
    //
    }
(Можете перезапустить приложение, открыть swagger и посмотреть что поменялось)

Для более информативного отображения информации о контроллерах, переходим в каждый контроллер и 
проставляем следующую аннотацию **@Tag**, пример:

    @Tag(name="Здесь пишем название контроллера", description="Здесь пишем описание контролера")
    @RestController
    @RequestMapping("/api/managers")
    @RequiredArgsConstructor
    public class ManagerController {

(Можете перезапустить приложение, открыть swagger и посмотреть что поменялось в названии контроллеров)

Для более информативного отображения информации о методах наших контроллеров, переходим в каждый контроллер,
идем к нужному методу и проставляем над ним следующую аннотацию **@Operation**, пример:

    @Operation(
        summary = "Список менеджеров",
        description = "Позволяет получить полный список все менеджеров в системе"
    )
    @GetMapping
    public List<ManagerDto> getAll() {

(Можете перезапустить приложение, открыть swagger и посмотреть что поменялось в названии методов)

Добавляем описание параметров запроса с помощью аннотации **@Parameter**, пример 

    @Operation(
        summary = "Удаление менеджера",
        description = "Позволяет удалить конкретного менеджера из системы"
    )
    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") @Parameter(description = "Идентификатор менеджера") long id) {

(Можете перезапустить приложение, открыть swagger и посмотреть что поменялось в описании параметров)

В описании структур данных, что мы должны передавать в наши методы отображаются наши наименования полей в json,
что не всегда понятно, поэтому можно и в dto объектах, что передаем в rest дать описание имен для полей. Для
это в самом dto классе, над полем проставляем аннотацию **@Schema** и укажем значение, которое будет,
как пример в swagger для этого параметра, пример:

    @Schema(description = "Имя менеджера",defaultValue = "Alex")
    private String firstName;

**Аутентификация для swagger**

Теперь осталось решить очень важный вопрос! Так как у нас в проекте часть методов защищено аутентификацией,
то мы не сможем выполнить их из интерфейса swagger без передачи логина и пароля, добавим этот функционал:

В класс конфигурации **OpenApiConfig**, который создавали ранее, нужно, над классом добавить еще одну аннотацию,
определяющую схему аутентификации для swagger :

    @SecurityScheme(
        name = "basicauth",
        in = SecuritySchemeIn.HEADER,
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
    )

Полный класс будет выглядеть так:

    @OpenAPIDefinition(
        info = @Info(
        title = "Bank application api",
        description = "Bank application", version = "1.0.0",
            contact = @Contact(
            name = "Alex Alexov",
            email = "alex@alexov.com",
            url = "http://alexov.com"
                                )
                    )
    )
    @SecurityScheme(
        name = "basicauth",
        in = SecuritySchemeIn.HEADER,
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
                    )
    public class OpenApiConfig {
    //
    }

После этого на главной странице swagger, над списком методов, справа,
появится кнопка с замком и названием Authorize, где можно ввести логин и пароль пользователя, 
под которым будет работать наш swagger и вызывать методы. !!! Что бы все работало этот тестовый пользователь
уже должен существовать в нашей системе.

Осталось только определить, какие методы нашего api будут использовать эту аутентификацию, для этого
переходим наши контроллеры, и над теми методами, что должны вызываться из swagger с помощью аутентификации 
в заголовке, проставляем аннотацию **@SecurityRequirement** с именем нашей схемы, что задали в конфигурации, пример:

    @Operation(
        summary = "Список менеджеров",
        description = "Позволяет получить полный список все менеджеров в системе"
    )
    @SecurityRequirement(name = "basicauth")
    @GetMapping
    public List<ManagerDto> getAll() {


**@SecurityRequirement(name = "basicauth")** данная аннотация должна содержать имя схемы указанной в конфигурации, 
в поле name (name = "basicauth"), мы сделали это ранее.

Теперь на UI swagger, у тех методов, которые мы пометили аннотацией, появился закрытый замок, если на него нажать,
то отобразятся доступные способы аутентификации, и если мы залогинились по кнопке выше, то там будет наш вариант.
Теперь можно вызывать защищенные spring security методы и получать ответы из swagger.

**Addons:** 

Если нет желания делать отдельный класс для конфигурации swagger, то можно аннотацию с описанием проекта и схемой
безопасности перенести в основной класс, пример:

    @SpringBootApplication
    @OpenAPIDefinition(
        info = @Info(title = "Bank application api", description = "Bank application", version = "1.0.0",
        contact = @Contact(name = "Alex Alexov", email = "alex@alexov.com", url = "http://alexov.com")))
    @SecurityScheme(name = "basicauth", in = SecuritySchemeIn.HEADER, type = SecuritySchemeType.HTTP, scheme = "basic")
    public class FinApp {

        public static void main(String[] args) {
            SpringApplication.run(FinApp.class, args);
        }
    }

Если необходимо убрать из документации swagger контроллер или метод, то достаточно проставить аннотацию **@Hidden**,
над целым контроллером или отдельным методом, тогда в swagger они не появятся.