# 🛡️ Anti-Fraud Banking API

![Java 21](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.2-brightgreen?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-blue?logo=postgresql)
![Spring Security](https://img.shields.io/badge/Security-JWT%20Stateless-green?logo=springsecurity)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker)
![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger%203.1-85EA2D?logo=swagger)

API RESTful bancária de alta confiabilidade desenvolvida para análise, prevenção e bloqueio de fraudes financeiras em tempo real. A aplicação conta com persistência em nuvem gerenciada via Supabase (PostgreSQL), controle de acesso stateless com tokens JWT, validações rigorosas de payload e documentação interativa via OpenAPI / Swagger UI.

---

## 🎯 Regras de Negócio e Motor Anti-Fraude

Cada transação submetida é inspecionada pelo motor de risco e classificada de forma síncrona em um dos três status: `APROVADA`, `NEGADA` ou `EM_ANALISE`.

| Regra | Condição de Acionamento | Parecer da API |
| :--- | :--- | :--- |
| **Velocity Check** | Mais de 3 transações efetuadas pela mesma conta de origem em menos de 2 minutos | **NEGADA** (Bloqueio automático por alta frequência de disparos) |
| **Limite Noturno** | Transação com valor superior a R$ 1.000,00 enviada entre 20h00 e 06h00 | **NEGADA** (Bloqueio preventivo por conformidade de horário restrito) |
| **Auditoria Manual (High-Risk)** | Transação unitária com valor superior a R$ 10.000,00 | **EM_ANALISE** (Retenção preventiva para checagem operacional humana) |
| **Transação Regular** | Operação com parâmetros válidos, limites adequados e horário padrão | **APROVADA** (Liberação imediata para liquidação financeira) |

---

## 🛠️ Tecnologias Utilizadas

* **Java 21 (LTS):** Recursos modernos da linguagem com Records e Virtual Threads.
* **Spring Boot 4.1.2:** Estrutura base da arquitetura web RESTful e injeção de dependências.
* **Spring Security & Java-JWT (Auth0):** Autenticação stateless, criptografia de senhas com BCrypt e validação de tokens Bearer.
* **Spring Data JPA & Hibernate:** Mapeamento objeto-relacional e gerenciamento de queries transacionais.
* **PostgreSQL (Supabase):** Banco de dados relacional hospedado em infraestrutura de nuvem.
* **Jakarta Bean Validation:** Validação de integridade de dados na camada de entrada (DTOs).
* **SpringDoc OpenAPI 3.1:** Documentação técnica interativa e testes de contrato via Swagger UI.
* **JUnit 5 & Mockito:** Cobertura de 10 testes automatizados (unitários e de integração com MockMvc).
* **Docker:** Estrutura multi-stage build para geração de imagens leves sobre Alpine Linux.

---

## 📌 Principais Endpoints da API

### Autenticação (`/auth`)
* `POST /auth/login` — Autentica as credenciais (`login`, `senha`) e retorna o Bearer Token JWT.

### Transações Financeiras (`/transacoes`)
* `POST /transacoes` — Submete uma nova transação financeira para análise em tempo real.
* `GET /transacoes` — Lista o histórico de transações persistidas e os respectivos pareceres.

> 🔒 **Autenticação:** Todas as rotas sob `/transacoes` exigem o envio do cabeçalho HTTP `Authorization: Bearer <SEU_TOKEN_JWT>`.

---

## 📖 Documentação Interativa (Swagger UI)

Com o servidor em execução, acesse a interface web interativa pelo navegador:

```text
http://localhost:8080/swagger-ui.html
```

Use o botão **Authorize** no canto superior direito para colar o seu token JWT e realizar testes interativos diretamente na interface visual.

---

## ⚙️ Configuração e Execução Local

### 1. Configurar Variáveis de Ambiente
O projeto inclui o arquivo modelo `src/main/resources/application.properties.example`. Duplique-o como `application.properties` e preencha com suas credenciais do banco:

```properties
spring.datasource.url=jdbc:postgresql://db.SEU_PROJETO.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA_AQUI
spring.datasource.driver-class-name=org.postgresql.Driver
```

### 2. Executar a Aplicação
No terminal do projeto, execute via Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Ou execute diretamente pelo binário compilado `.jar`:

```bash
# Compilar e empacotar
./mvnw clean package

# Executar o binário
java -jar target/antifraude-0.0.1-SNAPSHOT.jar
```

---

## 🧪 Suíte de Testes Automatizados

A aplicação possui 10 testes cobrindo regras de negócio, infraestrutura de segurança e controladores web:

```bash
./mvnw test
```

* **`TransacaoServiceTest` (6 testes unitários):** Valida velocity check, limites noturnos, margens de análise manual e fluxo padrão de aprovação.
* **`AntifraudeApplicationTests` (4 testes de integração):** Valida inicialização do contexto Spring, geração de JWT no login, validação de inputs inválidos (HTTP 400) e listagem de transações com MockMvc.

---

## 🐳 Execução via Docker (Multi-Stage)

O arquivo `Dockerfile` está otimizado para não expor código-fonte e operar com usuário sem privilégios de root:

```bash
# Construção da imagem Docker
docker build -t antifraude-api:latest .

# Inicialização do container com passagem das credenciais
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://db.SEU_PROJETO.supabase.co:5432/postgres" \
  -e SPRING_DATASOURCE_USERNAME="postgres" \
  -e SPRING_DATASOURCE_PASSWORD="SUA_SENHA_AQUI" \
  --name antifraude-container antifraude-api:latest
```

---

## 👤 Autor

Desenvolvido por **Alex Rodrigo**  
GitHub: [@alexrodrigodev](https://github.com/alexrodrigodev)
