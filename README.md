
# Password Vault: A backend API application for password management

This application is built to provide a backend API with microservice architecture for password management.

## Features
- Privacy and security for password management
- Local DB, so its secure!
- Password encryption
- Tagging, searching, and filtering
- Sharing and collaboration with friends and family
- Full audit trail

## Tech Stack
- Java 21
- Spring Boot 4.0.0
- Gradle (Groovy DSL)
- Docker
- MySQL ready

## Build image & Local Run with Docker
### To make application UP
```bash
docker compose -f docker/docker-compose.yml up -d
```
### To make application DOWN
```bash
docker compose -f docker/docker-compose.yml down
```

## Test with curl
### 1. CREATE
```bash
curl -X POST http://localhost:8080/api/v1/passwords \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "siddhartha",
    "name": "GitHub",
    "websiteUrl": "github.com",
    "email": "siddhartha@github.com",
    "password": "iAm_PasSword",
    "tags": ["work", "social"],
    "notes": "2FA enabled"
  }'
```
### 2. GET ALL
```bash
curl "http://localhost:8080/api/v1/passwords/user/siddhartha"
```
### 3. GET BY TAG
```bash
curl "http://localhost:8080/api/v1/passwords/user/siddhartha/tags/work"
```
### 4. SEARCH
```bash
curl "http://localhost:8080/api/v1/passwords/user/siddhartha/search?q=github"
```
### 5. UPDATE
```bash
curl -X PUT http://localhost:8080/api/v1/passwords/{UUID} \
  -H "Content-Type: application/json" \
  -d '{"password": "newSecurePass456!"}'
```
### 6. DELETE
```bash
curl -X DELETE "http://localhost:8080/api/v1/passwords/{UUID}?userId=siddhartha"
```