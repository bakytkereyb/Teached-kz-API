# teached.kz - Backend

## Описание проекта
Это backend часть веб-приложения teached.kz, предназначенная для образовательных целей. Проект включает в себя аутентификацию пользователей, управление анкетами и компетенциями через REST API.

## Технологический стек
- **Язык программирования:** Java 17
- **Сборщик проекта:** Maven
- **База данных:** PostgreSQL
- **Фреймворк:** Spring Boot

## Требования
Убедитесь, что на вашем компьютере установлены следующие инструменты:
- JDK 17
- Maven
- PostgreSQL

## Настройка проекта

### Шаг 1: Установка и настройка PostgreSQL
1. Установите PostgreSQL.
2. Создайте базу данных `teacheddb`.

### Шаг 2: Конфигурация `application.properties`
Отредактируйте файл `src/main/resources/application.properties`, указав следующие параметры:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/teacheddb
spring.datasource.username=ваш_логин
spring.datasource.password=ваш_пароль
spring.mail.host=адрес_почтового_сервера
spring.mail.username=логин_почты
spring.mail.password=пароль_почты
```
### Шаг 3: Настройка класса `EnvironmentProperties`
Настройте необходимые переменные среды в классе `EnvironmentProperties`, который находится в папке `constants`.

## Запуск приложения
Для запуска приложения используйте следующую команду Maven:
```
mvn spring-boot:run
```
Приложение запустится на порту `8080`.

## API Endpoints
### Аутентификация
* `POST /api/auth/login` - Авторизация пользователя. Требует `username` и `password`. Возвращает `access_token`.

### Банк компетенций
* `GET /api/competence-bank/get` - Получение списка компетенций, доступных для пользователя.

### Прохождение анкет
* `POST /api/questionnaire-bank/{id}/pass` - Сдача анкеты по указанному `ID`.

### Регистрация пользователей
* `POST /api/user/save` - Регистрация нового пользователя.

## Документация API
Подробную документацию по API можно найти в Swagger, доступном по адресу `/docs` после запуска приложения.
