# SpringBoot JWT Security Project

Ce projet est une API **Spring Boot** avec authentification **JWT** (access token + refresh token), gestion des rôles, et endpoints sécurisés avec Spring Security.

## ✨ Fonctionnalités

- Authentification via `/auth/login`
- Inscription via `/auth/register`
- Renouvellement de token via `/auth/refresh`
- Déconnexion (révocation du refresh token) via `/auth/logout`
- Endpoint protégé par rôle admin (`/test`)
- Persistance des utilisateurs et refresh tokens en base MySQL

## 🧱 Stack technique

- Java 17
- Spring Boot 4
- Spring Security
- Spring Data JPA
- MySQL
- JJWT (JSON Web Token)
- Docker / Docker Compose

## 📂 Structure principale

- `controller/` : contrôleurs REST (`AuthController`, `TestController`)
- `services/` : logique métier (authentification, refresh token)
- `security/` : utilitaires JWT, filtre d’authentification, UserDetails
- `entities/` : entités JPA (`User`, `RefreshToken`)
- `repository/` : accès base de données
- `config/` : configuration sécurité globale
- `exceptions/` : exceptions métier + gestion globale des erreurs

## ⚙️ Configuration

La configuration par défaut est dans `src/main/resources/application.properties`.

Exemples de propriétés importantes :

- `spring.jwt.signing-key` : clé de signature JWT
- `spring.jwt.expiration-minutes` : durée de vie de l’access token
- `spring.jwt.refresh-expiration-days` : durée de vie du refresh token
- `spring.datasource.*` : configuration MySQL

> ⚠️ En local, adaptez les credentials DB selon votre environnement.

## 🚀 Lancer le projet

### Option 1 — En local (Maven)

1. Démarrer MySQL.
2. Créer la base (si nécessaire) et ajuster `application.properties`.
3. Compiler et démarrer l’application :

```bash
./mvnw clean spring-boot:run
```

L’API sera disponible sur `http://localhost:8080`.

### Option 2 — Avec Docker Compose

```bash
docker compose -f Docker-compose.yml up --build
```

Services démarrés :

- MySQL : `localhost:3306`
- API Spring Boot : `localhost:8080`

## 🔐 Endpoints d’authentification

### 1) Register

`POST /auth/register`

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "StrongPass123!"
}
```

Réponse attendue : `201 Created`.

### 2) Login

`POST /auth/login`

```json
{
  "email": "john@example.com",
  "password": "StrongPass123!"
}
```

Réponse :

```json
{
  "accessToken": "...",
  "refreshToken": "..."
}
```

### 3) Refresh

`POST /auth/refresh`

```json
{
  "refreshToken": "..."
}
```

Renvoie un nouveau couple `accessToken` / `refreshToken`.

### 4) Logout

`POST /auth/logout`

```json
{
  "refreshToken": "..."
}
```

Réponse attendue : `204 No Content`.

## 🔒 Endpoint protégé

`GET /test`

- Nécessite un token JWT valide
- Nécessite le rôle `ADMIN`

Exemple d’en-tête :

```http
Authorization: Bearer <access_token>
```
---
