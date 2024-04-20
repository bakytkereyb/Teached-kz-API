# teached.kz - Backend

## Project Description
This is the backend part of the teached.kz web application, designed for educational purposes. The project includes user authentication, management of questionnaires and competencies through a REST API.

## Technology Stack
- **Programming Language:** Java 17
- **Project Builder:** Maven
- **Database:** PostgreSQL
- **Framework:** Spring Boot

## Requirements
Ensure the following tools are installed on your computer:
- JDK 17
- Maven
- PostgreSQL

## Project Setup

### Step 1: Installation and Configuration of PostgreSQL
1. Install PostgreSQL.
2. Create a database `teacheddb`.

### Step 2: Configuration of `application.properties`
Edit the `src/main/resources/application.properties`, file with the following settings:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/teacheddb
spring.datasource.username=ваш_логин
spring.datasource.password=ваш_пароль
spring.mail.host=адрес_почтового_сервера
spring.mail.username=логин_почты
spring.mail.password=пароль_почты
```
### Step 3: Setting Up `EnvironmentProperties`
Configure the necessary environment variables in the `EnvironmentProperties` class, located in the `constants` folder.

## Application Launch
To launch the application, use the following Maven command:
```
mvn spring-boot:run
```
The application will start on port `8080`.

## API Endpoints
### Authentication
* `POST /api/auth/login` - User authorization. Requires `username` and `password`. Returns `access_token`.

### Competence Bank
* `GET /api/competence-bank/get` - Retrieving a list of competencies available to the user.

### Passing Questionnaires
* `POST /api/questionnaire-bank/{id}/pass` - Submission of a questionnaire for a specified `ID`.

### User Registration
* `POST /api/user/save` - Registration of a new user.

## API Documentation
Detailed API documentation can be found in Swagger, available at `/docs` after launching the application.
