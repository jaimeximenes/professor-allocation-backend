# Atividade Backend - Gerenciamento de Professores e Alocações

Este projeto consiste no desenvolvimento do backend para um sistema de gerenciamento de professores, departamentos, cursos e alocações de horários. A aplicação foi construída utilizando tecnologias modernas: **Java 25**, **Spring Boot 4**, **Jakarta Persistence (JPA)**, **Spring Data JPA** e **MySQL** como banco de dados.

A documentação interativa da API está disponível via **Swagger / OpenAPI** e a segurança de origem foi configurada via **CORS**.

---

## 👥 Integrantes do Grupo

Os integrantes do grupo (em ordem alfabética) são:

1. **Jaime Ximenes**
2. **Ludmilla Táfny**
3. **Ruan Santos**

---

## 🛠️ Divisão de Responsabilidades e Implementações de Melhoria

Para alcançar a nota máxima no trabalho, foram aplicados três importantes pilares arquiteturais, distribuídos da seguinte forma entre os integrantes:

### 1. Jaime Ximenes -> Manipulador de Exceções (Exception Handlers)
* **O que foi feito:** Toda a lógica de blocos `try/catch` manuais e repetitivos foi removida das classes de controle. Foi implementada uma centralização do tratamento de erros através de um manipulador de exceções global (`GlobalExceptionHandler`) anotado com `@RestControllerAdvice`.
* **Decisões de Implementação:** 
  * Captura de `ResourceNotFoundException`: Retorna o status `404 Not Found` quando um registro (como Professor ou Departamento) não é localizado no banco de dados.
  * Captura de `MethodArgumentNotValidException`: Intercepta erros de validação do Spring Validation na entrada de dados, mapeando cada campo incorreto com sua respectiva mensagem em um corpo JSON legível.
  * Captura de `IllegalArgumentException`: Retorna o status `400 Bad Request` para violações de regras de negócios (como colisão de horários ou intervalos de tempo inválidos).
  * Captura genérica de `Exception`: Captura erros inesperados de infraestrutura para evitar o vazamento de stack traces internos, retornando `500 Internal Server Error`.

### 2. Ludmilla Táfny -> Uso de DTO (Data Transfer Object)
* **O que foi feito:** Foram criadas classes DTO (`DepartmentDTO`, `ProfessorDTO`, `CourseDTO`, `AllocationDTO`) para receber e retornar os dados na camada de controle.
* **Decisões de Implementação:**
  * As entidades de banco de dados (JPA) não são expostas na API REST, protegendo a integridade interna e facilitando a evolução do modelo de dados.
  * A utilização de DTOs planos contornou o problema de **referência cíclica (infinite recursion)** na serialização de objetos JSON (onde Professor referencia Departamento, que por sua vez referencia a lista de Professores).
  * Foi criado o DTO estruturado de resposta de erros `ErrorResponse` (contendo timestamp, status code, erro, mensagem explicativa, rota requisitada e lista de validações violadas), enriquecendo o corpo da resposta em fluxos de erro.

### 3. Ruan Santos -> Uso de Validadores
* **O que foi feito:** Implementação de validações declarativas na entrada de dados usando anotações do framework **Jakarta Validation** nos atributos dos DTOs e validações adicionais de regras de negócio.
* **Decisões de Implementação:**
  * No `ProfessorDTO`, o campo `cpf` é validado usando expressão regular (`@Pattern(regexp = "\\d{11}")`) para aceitar estritamente 11 caracteres numéricos.
  * Todos os DTOs utilizam `@NotBlank` e `@Size(max = 100)` para campos de texto e `@NotNull` para chaves estrangeiras/associações obrigatórias.
  * No `AllocationService`, valida-se se a hora de início (`startHour`) ocorre antes da hora de término (`endHour`).
  * Implementação da **validação de colisão de horários**: impede que o mesmo professor seja alocado no mesmo dia da semana em horários que se sobreponham.

---

## 📂 Estrutura de Pacotes do Projeto

```text
com.project.professor.allocation
│
├── Application.java                   # Inicialização da aplicação Spring Boot
│
├── config                             # Configurações globais do sistema
│   ├── CorsConfig.java                # Habilita requisições CORS
│   └── OpenApiConfig.java             # Configuração da documentação Swagger UI
│
├── entity                             # Entidades JPA (Camada de Persistência)
│   ├── Allocation.java
│   ├── Course.java
│   ├── Department.java
│   └── Professor.java
│
├── repository                         # Interfaces Spring Data JPA (Acesso a dados)
│   ├── AllocationRepository.java
│   ├── CourseRepository.java
│   ├── DepartmentRepository.java
│   └── ProfessorRepository.java
│
├── service                            # Camada de Serviços (Regras de Negócio)
│   ├── AllocationService.java         # CRUD de Alocações e validações de colisão
│   ├── CourseService.java             # CRUD de Cursos e busca por professor
│   ├── DepartmentService.java         # CRUD de Departamentos e busca por professor
│   └── ProfessorService.java          # CRUD de Professores e busca por nome
│
├── dto                                # Objetos de Transferência (DTOs)
│   ├── AllocationDTO.java
│   ├── CourseDTO.java
│   ├── DepartmentDTO.java
│   └── ProfessorDTO.java
│
├── exception                          # Tratamento de Exceções Global
│   ├── ErrorResponse.java
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
│
└── controller                         # Controladores REST (Pontos de entrada da API)
    ├── AllocationController.java
    ├── CourseController.java
    ├── DepartmentController.java
    └── ProfessorController.java
```

---

## 🚀 Como Configurar e Executar a Aplicação

### Pré-requisitos
* Java JDK 25 instalado
* Apache Maven instalado
* MySQL Server rodando localmente

### 1. Configuração do Banco de Dados
No arquivo [`application.properties`](file:///c:/github/atividade_backend/src/main/resources/application.properties), configure as credenciais do seu banco de dados MySQL local. O banco de dados `db_allocation` será criado automaticamente na primeira execução se não existir:
```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/db_allocation?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### 2. Compilar e Executar
Abra o terminal no diretório raiz do projeto e execute:
```bash
mvn clean spring-boot:run
```

### 3. Documentação Swagger UI
Com o servidor rodando, acesse a documentação interativa da API pelo navegador no endereço:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

---

## 🗓️ Detalhes de Entrega

* **Meio de entrega:** Link de repositório público no GitHub.
* **Data Limite de Entrega:** Até o dia **21/08/2026**.
