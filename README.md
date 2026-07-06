# SRT Todo List

A full-stack Todo List application with a Spring Boot backend, a Node.js/Vite frontend, and a PostgreSQL database. This guide walks you through installing all prerequisites and setting up the project locally.

## Project Structure

```
srt-todo-list/
├── backend/              # Spring Boot (Java) REST API
├── frontend/             # Vite-based frontend (JS)
├── docker-compose.yaml   # Orchestrates db + backend + frontend
└── .gitignore
```

## Prerequisites

Install the following tools before you begin:

| Tool | Recommended Version | Purpose |
|------|----------------------|---------|
| [Docker](https://docs.docker.com/get-docker/) & Docker Compose | Latest | Run PostgreSQL, backend, and frontend as containers |
| [JDK](https://adoptium.net/) | 17+ | Build/run the Spring Boot backend |
| [Node.js](https://nodejs.org/) | 18+ | Build/run the frontend |
| [PostgreSQL](https://www.postgresql.org/download/) | 15+ | Only needed if you run the database **without** Docker |
| Git | Latest | Clone the repository |

You can verify installations with:

```bash
docker --version
java -version
node --version
npm --version
```

## 1. Clone the Repository

```bash
git clone https://github.com/nhannk15/srt-todo-list.git
cd srt-todo-list
```

## 2. Set Up the PostgreSQL Database

You have two options: let Docker create the database for you, or set it up manually.

### Option A — Using Docker (recommended)

The `docker-compose.yaml` file already spins up a `postgres:15` container and reads credentials from a `.env` file (see step 4). You don't need to create the database manually — just make sure the `.env` file is configured, then run:

```bash
docker compose up -d db
```

### Option B — Manual PostgreSQL installation

If you prefer running PostgreSQL locally instead of in Docker:

1. Install PostgreSQL and start the service.
2. Open `psql` (or any PostgreSQL client) and create the database:

```sql
CREATE DATABASE todolist;
```

3. Make sure the user/password you configure in `application.yaml` (step 3) has access to this database.

> Note: The backend uses `ddl-auto: create`, so Hibernate will automatically create the required tables the first time the backend starts. You only need the empty `todolist` database to exist beforehand.

## 3. Configure `application.yaml`

The backend configuration lives at `backend/src/main/resources/application.yaml`. Update it with your own values (database credentials, Google OAuth2 client, JWT secret, etc.):

```yaml
spring:
    application:
        name: backend

    # DataSource Configuration
    datasource:
        driver-class-name: org.postgresql.Driver
        url: jdbc:postgresql://localhost:5432/todolist
        username: <your-db-username>
        password: <your-db-password>

    # JPA Configuration
    jpa:
        hibernate:
            ddl-auto: create
            naming:
                physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
        show-sql: false

    # Security Configuration
    security:
        oauth2:
            client:
                registration:
                    google:
                        client-id: <your-google-client-id>
                        client-secret: <your-google-client-secret>

logging:
  level:
    org:
        springframework:
            security: INFO

jwt:
    secret-key: <your-jwt-secret-key>

frontend:
    base-url: http://localhost:5173
```

**Notes:**
- `datasource.url`, `username`, and `password` must match the database you created in step 2.
- `security.oauth2.client.registration.google.client-id` / `client-secret` come from your [Google Cloud Console](https://console.cloud.google.com/apis/credentials) OAuth2 credentials.
- `jwt.secret-key` should be a long, random string used to sign JWT tokens (do not commit real secrets to version control).
- `frontend.base-url` should match wherever your frontend is running (default `http://localhost:5173`).

## 4. Configure the `.env` File

`docker-compose.yaml` references an `.env` file at the project root for the PostgreSQL container and other environment variables. Create a `.env` file in the project root:

```bash
touch .env
```

Add the following variables (adjust values to match what you used in `application.yaml`):

```env
# PostgreSQL container settings
POSTGRES_USER=<your-db-username>
POSTGRES_PASSWORD=<your-db-password>
POSTGRES_DB=todolist

# Backend
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/todolist
SPRING_DATASOURCE_USERNAME=<your-db-username>
SPRING_DATASOURCE_PASSWORD=<your-db-password>

# Google OAuth2
GOOGLE_CLIENT_ID=<your-google-client-id>
GOOGLE_CLIENT_SECRET=<your-google-client-secret>

# JWT
JWT_SECRET_KEY=<your-jwt-secret-key>

# Frontend
FRONTEND_BASE_URL=http://localhost:5173
```

> Add `.env` to `.gitignore` (it should already be listed) so secrets are never committed.

## 5. Run the Project

### Option A — Run everything with Docker Compose

```bash
docker compose up -d --build
```

This builds and starts three containers:
- `todolist_db` — PostgreSQL on port `5432`
- `todolist_backend` — Spring Boot API on port `8080`
- `todolist_frontend` — Frontend dev server on port `5173`

Check status with:

```bash
docker compose ps
docker compose logs -f
```

Stop everything with:

```bash
docker compose down
```

### Option B — Run backend and frontend manually

**Backend:**

```bash
cd backend
./mvnw spring-boot:run
# or, if using Gradle:
# ./gradlew bootRun
```

**Frontend** (in a separate terminal):

```bash
cd frontend
npm install
npm run dev
```

## 6. Access the Application

- Frontend: [http://localhost:5173](http://localhost:5173)
- Backend API: [http://localhost:8080](http://localhost:8080)

## Troubleshooting

- **Backend can't connect to the database**: confirm `todolist` database exists and credentials in `application.yaml` / `.env` match.
- **Google login fails**: verify your OAuth2 redirect URI is registered in Google Cloud Console and matches your backend's callback URL.
- **Port already in use**: change the conflicting port mapping in `docker-compose.yaml` or stop the process using that port.
