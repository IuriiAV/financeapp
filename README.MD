**1. Предварительно для работы Liquibase :**

После подключения Lb , все таблицы и любые изменения в базе данных будем создавать через Lb.
Поэтому, закомментируем проперти авто генерации таблиц :

      #spring.jpa.hibernate.ddl-auto=update
      #spring.jpa.generate-ddl=true

Для базы данных H2, ничего не делаем, она при каждом запуске новая. 
Для базы данных MySql нужно поменять базу на чистую, так как теперь будем все делать через Lb.

**2. Подключение Liquibase :**

При работе со spring boot , подключение очень простое :
Добавляем зависимость :

         <dependency>
            <groupId>org.liquibase</groupId>
            <artifactId>liquibase-core</artifactId>
         </dependency>

В папке с ресурсами создаем каталог db и внутри него каталог changelog - в этом каталоге будет находиться скрипт
со всеми изменения в для базы данных.
В данный каталог поместим файл db.changelog-master.xml, пример данного файла присутствует в проекте.
И в проперти укажем что изменения для Lb будут именно в этом файле
spring.liquibase.change-log=classpath:/db/changelog/db.changelog-master.xml

**3. Работа с БД.**

Для каждого изменения создается секция changeset,  пример :

    <changeSet id="202308111253456" author="Anton Antonov">
   
    </changeSet>

В поле id указываем уникальный идентификатор для этого changeSet, в одном проекте не должно быть дублирующихся id.
Указываете автора изменений.

Создадим таблицу managers с помощью Lb:

      <changeSet id="202308111253456" author="Anton Antonov">
        <comment>Add managers table</comment>
        <createTable tableName="managers">
            <column autoIncrement="true" name="id" type="BIGINT">
                <constraints primaryKey="true" nullable="false" primaryKeyName="manager_pkey"/>
            </column>
            <column name="first_name" type="VARCHAR(255)"/>
            <column name="last_name" type="VARCHAR(255)"/>
        </createTable>
      </changeSet>


Колонок минимум только для примера.

Создадим таблицу clients с помощью Lb:

    <changeSet id="202308121253758" author="Anton Antonov">
        <comment>Add clients table</comment>
        <createTable tableName="clients">
            <column name="id" type="BINARY(16)">
                <constraints primaryKey="true" nullable="false" primaryKeyName="client_pkey"/>
            </column>
            <column name="first_name" type="VARCHAR(255)"/>
            <column name="last_name" type="VARCHAR(255)"/>
            <column name="manager_id" type="BIGINT">
                <constraints foreignKeyName="manager_fkey"
                             referencedTableName="managers"
                             referencedColumnNames="id" nullable="false"/>
            </column>
        </createTable>
    </changeSet>

После запуска вашего приложения , будут созданы указанные таблицы в том порядке, в котором они
написаны в файле.
Так же появятся две таблицы, databasechangelog в которой будет храниться перечень исполненных changeSet
и они не будут выполняться повторно и таблица databasechangeloglock тех таблица на время применения изменений.

**4. Простановка связей между таблицами:**

   Для таблицы clients мы создали в changeSet колонку , которая будет являться внешним ключом для менеджера.
   В классе Client прописали это поле как:

      @ManyToOne(cascade = CascadeType.ALL)
      @JoinColumn(name = "manager_id", referencedColumnName = "id")
      private Manager manager;

   Для класса таблицы managers никаких доп полей не создавали, но в классе Manager
   прописали поле:

      @OneToMany(mappedBy = "manager")      // В поле mappedBy ссылка на то , как имя поле называется в классе Client
      List<Client> clients = new ArrayList<>();

   Теперь связи работают отлично и если выбрать менеджера, то к нему в выборку попадут все его клиенты.
   И наоборот, при выборе клиента - в выборке будет и его менеджер и клиенты этого менеджера.
   
   Все это можно проверить на результатах выборки в сервисах. Но если отправить такие объекты в REST,
   То при попытке преобразования в JSON будет рекурсивная ошибка(как на уроке)

**5. Передача данных через JSON**

   Для избежания ошибки с переполнением стека при преобразовании объекта в JSON(ошибки как на занятии),
   Есть три варианта решения вопроса(2 без DTO, 1 c DTO).

   **a) Отправка в JSON Объектов из базы данных. Исключаем преобразование связного объекта на одной стороне.**

   Необходимо использовать две дополнительные аннотации:
   
   В классе Client, над полем manager - @JsonBackReference (исключит вывод этого поля)

   В классе Manager над полем List<Client> clients = new ArrayList<>(); - @JsonManagedReference

   **b) Отправка в JSON Объектов из базы данных. Выводим все поля и все связи.**

   Необходимо проставить над классом Manager следующую аннотацию:

      @JsonIdentityInfo(
      generator = ObjectIdGenerators.PropertyGenerator.class,
      property = "id")
   
   Теперь при выборке менеджера будут выбраны все его клиенты и наоборот, при выборе клиента будет выбран и его менеджер
   и клиенты этого менеджера(что не совсем удобно)

   с) **Не использовать доп аннотации.** 
      
   Использовать ClientDTO и ManagerDTO , что бы ограничивать поля передаваемые в JSON
   Данный вариант представлен в примере.
      
      