# auth-service — Autenticação e Emissão de JWT

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18.0-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED.svg)](https://www.docker.com/)

Microsserviço de autenticação do sistema **Cheffy**, responsável pelo registro de usuários, emissão de tokens JWT assinados com RSA (RS256) e exposição da chave pública via JWKS para validação distribuída pelos demais serviços.

---

## O que é o auth-service?

O `auth-service` é o guardião de identidade da plataforma Cheffy. Ele centraliza toda a lógica de autenticação, garantindo que somente usuários válidos possam obter tokens, e que esses tokens possam ser verificados de forma independente por qualquer serviço do ecossistema sem necessidade de consulta centralizada.

**Responsabilidades principais:**
- Registro de novos usuários com senha segura (BCrypt + regras de complexidade)
- Autenticação via login/senha com emissão de JWT assinado (RS256)
- Troca de senha para usuários autenticados
- Exposição da chave pública RSA via `/.well-known/jwks.json` para validação de tokens pelos demais serviços

---

## Arquitetura Interna

O `auth-service` segue **Arquitetura Hexagonal (Ports & Adapters)**, organizado em 4 camadas:

```
adapter/input   →  Controllers (REST) · JwtAdapter · RsaKeyProviderAdapter
                   PasswordEncoderAdapter · CurrentUserAdapter
application     →  Use Cases (RegisterUser, Login, ChangePassword, GetPublicKey)
                   Services (AuthService, KeyService)
                   Ports de entrada e saída
domain          →  AuthUser (entidade JPA) · InvalidPasswordException
                   LoginAlreadyExistsException
adapter/output  →  AuthUserRepository (implementação JPA)
config          →  SecurityConfig · JwkConfig · ApiExceptionHandler
```

**Princípio central:** as dependências sempre apontam para dentro. Os adapters são substituíveis sem afetar a lógica de negócio.

---

## Fluxos de Autenticação

### Registro de Usuário

```
POST /auth/register
  │
  ├─ Valida complexidade da senha (12+ chars, maiúsc., minúsc., dígito, símbolo)
  ├─ Verifica unicidade do login
  ├─ Verifica idempotência pelo externalId (retorna existente se já cadastrado)
  ├─ Codifica senha com BCrypt
  ├─ Persiste AuthUser no PostgreSQL
  └─ Retorna: { "id": "<UUID>" }
```

### Login e Emissão de Token

```
POST /auth/login
  │
  ├─ Busca usuário por login
  ├─ Verifica se o usuário está ativo
  ├─ Valida senha com BCrypt
  ├─ Gera JWT com claims customizados (RS256)
  └─ Retorna: { "token": "<JWT>" }
```

### Validação de Token (fluxo distribuído)

```
Outros serviços (cheffy-api, order-service)
  │
  ├─ GET /.well-known/jwks.json  ──▶  auth-service retorna chave pública RSA
  ├─ Recebem Bearer Token no header Authorization
  ├─ Validam assinatura RS256 localmente com a chave pública
  └─ Extraem claims sem precisar consultar o auth-service
```

### Troca de Senha

```
PATCH /auth/change-password  (requer Bearer Token)
  │
  ├─ Extrai usuário autenticado do SecurityContext (via JWT claims)
  ├─ Valida senha antiga com BCrypt
  ├─ Valida complexidade da nova senha
  ├─ Codifica e persiste nova senha
  └─ Retorna: 204 No Content
```

---

## Stack Técnica

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.5.7 |
| Segurança | Spring Security + OAuth2 Resource Server |
| JWT | JJWT 0.12.5 + Nimbus JOSE (RS256 com par RSA-2048) |
| Persistência | Spring Data JPA + Hibernate |
| Banco de dados | PostgreSQL 18.0 |
| Documentação | SpringDoc OpenAPI (Swagger) |
| Monitoramento | Spring Boot Actuator |
| Build | Maven |
| Containerização | Docker (multi-stage build) |

---

## Endpoints da API

**Base URL:** `http://localhost:8085`

| Método | Path | Auth | Descrição |
|---|---|---|---|
| `POST` | `/auth/register` | Público | Registra novo usuário |
| `POST` | `/auth/login` | Público | Autentica e retorna JWT |
| `PATCH` | `/auth/change-password` | Bearer Token | Altera senha do usuário autenticado |
| `GET` | `/.well-known/jwks.json` | Público | Chave pública RSA em formato JWKS |
| `GET` | `/swagger-ui.html` | Público | Documentação interativa da API |
| `GET` | `/actuator/**` | Público | Health checks e métricas |

### Exemplos de payload

**POST /auth/register**
```json
{
  "login": "usuario@email.com",
  "password": "Senha@Segura123",
  "externalId": "uuid-do-usuario-no-cheffy-api"
}
```

**POST /auth/login**
```json
{
  "login": "usuario@email.com",
  "password": "Senha@Segura123"
}
```

**PATCH /auth/change-password**
```json
{
  "oldPassword": "Senha@Segura123",
  "newPassword": "NovaSenha@456"
}
```

---

## Estrutura do JWT

O token emitido pelo `auth-service` contém os seguintes claims:

```json
{
  "sub": "<externalId>",      // ID do usuário no sistema externo (cheffy-api)
  "iss": "<JWT_ISSUER>",      // Identificador do emissor
  "login": "<username>",      // Login do usuário
  "ver": 1,                   // Versão do token (para invalidação futura)
  "authId": "<UUID>",         // ID interno do usuário no auth-service
  "iat": 1748218800,          // Emitido em (Unix timestamp)
  "exp": 1748222400           // Expira em (iat + JWT_EXPIRATION segundos)
}
```

O token é assinado com **RS256** (chave privada RSA-2048). A validação é feita com a chave pública disponível em `/.well-known/jwks.json`.

---

## Segurança

### RSA-2048 (RS256)
- Par de chaves gerado automaticamente no primeiro startup via `init-keys.sh`
- Chave privada: usada exclusivamente pelo `auth-service` para assinar tokens
- Chave pública: exposta via JWKS; usada pelos outros serviços para validar tokens sem depender do `auth-service`
- Identificador de chave (KID) gerado via SHA-256 do conteúdo da chave pública

### Senhas
- Armazenadas com **BCrypt** (sem reversibilidade)
- Regras de complexidade obrigatórias:
  - Mínimo de **12 caracteres**
  - Pelo menos **1 letra maiúscula**
  - Pelo menos **1 letra minúscula**
  - Pelo menos **1 dígito**
  - Pelo menos **1 símbolo** (não alfanumérico)

### Token Versioning
- O campo `tokenVersion` no banco permite invalidar todos os tokens de um usuário sem exigir troca de senha (útil para logout global ou comprometimento de conta).

### Sessão
- Política **STATELESS**: nenhuma sessão HTTP é mantida no servidor.

---

## Variáveis de Ambiente

| Variável | Descrição | Exemplo |
|---|---|---|
| `DB_HOST` | Host do PostgreSQL | `postgres` |
| `DB_PORT` | Porta do PostgreSQL | `5432` |
| `DB_NAME` | Nome do banco de dados | `auth_service` |
| `POSTGRES_PASSWORD` | Senha do PostgreSQL | `P4ssword!` |
| `PRIVATE_KEY_PATH` | Caminho da chave privada RSA | `/app/keys/private.pem` |
| `PUBLIC_KEY_PATH` | Caminho da chave pública RSA | `/app/keys/public.pem` |
| `KEY_DIR` | Diretório das chaves RSA | `/app/keys` |
| `JWT_EXPIRATION` | Tempo de expiração do token (segundos) | `3600` |
| `JWT_ISSUER` | Identificador do emissor no token | `cheffy-auth-service` |

---

## Como Executar Localmente

**Pré-requisitos:**
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado
- [Git](https://git-scm.com/) instalado
- Porta `8085` disponível

### 1. Clone o repositório

```bash
git clone https://github.com/leandrocfita/cheffy-microservices.git
cd cheffy-microservices/auth-service
```

### 2. Crie o arquivo `.env`

Crie um arquivo `.env` na raiz do projeto (este arquivo **não está versionado**):

```env
DB_HOST=postgres
DB_PORT=5432
DB_NAME=auth_service
POSTGRES_PASSWORD=P4ssword!
PRIVATE_KEY_PATH=/app/keys/private.pem
PUBLIC_KEY_PATH=/app/keys/public.pem
KEY_DIR=/app/keys
JWT_EXPIRATION=3600
JWT_ISSUER=cheffy-auth-service
JWT_SECRET=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b
```

### 3. Suba os contêineres

```bash
docker compose up --build
```

O script `init-keys.sh` (entrypoint do contêiner) gera automaticamente o par de chaves RSA em `./keys/` caso ainda não existam.

### 4. Verifique o serviço

```bash
curl http://localhost:8085/actuator/health
# {"status":"UP"}

curl http://localhost:8085/.well-known/jwks.json
# {"keys":[{"kty":"RSA","e":"AQAB","kid":"...","n":"..."}]}
```

### Comandos úteis

```bash
docker compose down       # para os contêineres (mantém os dados)
docker compose down -v    # para e remove volumes (apaga dados)
```

---

## CI/CD

O pipeline GitHub Actions (`.github/workflows/ci.yml`) executa automaticamente a cada push:

| Etapa | Descrição |
|---|---|
| **Build** | Compila o projeto com Maven (Java 21) |
| **Tests + Análise** | Executa `mvn verify` e envia relatório ao SonarQube |
| **Docker Hub** | Constrói e publica a imagem Docker (apenas após testes aprovados) |

---

## UIs de Administração

| Ferramenta | URL |
|---|---|
| Swagger UI | `http://localhost:8085/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8085/v3/api-docs` |
| Health Check | `http://localhost:8085/actuator/health` |
| JWKS Endpoint | `http://localhost:8085/.well-known/jwks.json` |

---

## 👥 Equipe

- Leandro Fita
- Igor Costa
- Rodrigo Ferreira
- Thiago Soares
- Victor Reis

## 📄 Licença

Este projeto foi desenvolvido como parte do Tech Challenge da FIAP e é disponibilizado para fins educacionais.

## 🤝 Contribuindo

Este é um projeto acadêmico, mas sugestões e feedback são bem-vindos!

## 📞 Contato

Para dúvidas ou sugestões, abra uma issue no repositório.

---

Desenvolvido pela equipe Cheffy - FIAP 2026
