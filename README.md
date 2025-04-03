# Movies Recommender Catalogue

## Description
The web application is a catalogue of movies of different genres.
Each movie has a title, description, film director, and list of actors, and image(s) from the main playbill.
Each movie is categorized in one or more genres. 
Only the administrator can create, remove and update the list of movies. 
Users can rate the movies according to different criteria 
The main feature of the system is that users can pick one movie and get the list
of similar movies and movies that were liked the most by other users watching the same movie.

## Microservices Overview

This project follows a **microservices architecture**, where each service is responsible for a specific domain.

### 1. Movie Service 🎬

- **Purpose:** Manages movies, their details, and genres.
- **Key Features:**
    - CRUD operations for movies (add, update, delete, get)
    - Assign genres to movies
    - Retrieve movie details
    - Store movie-related images

### 2. Film Person Service 🎭

- **Purpose:** Handles information about film industry professionals (actors, directors, screenwriters, etc.).
- **Key Features:**
    - CRUD operations for film persons
    - Store biography, date of birth, and image
    - Link film persons to movies based on their roles (actor, director, etc.)

### 3. User Service 👤

- **Purpose:** Manages users, their roles, and authentication.
- **Key Features:**
    - User registration & login
    - Role-based access control (ADMIN, USER)
    - Manage user profiles (update email, username)

### 4. Feedback Service 📝

- **Purpose**: Handles both user ratings and written reviews for movies.
- **Key Features**:
    - Users can rate movies (0–100%) in different categories (acting, directing, etc.)
    - Users can submit written reviews along with ratings
    - Retrieve all feedback (ratings + reviews) for a movie
    - Calculate an overall movie rating
    - Retrieve user-specific feedback
    - Allow administrators to moderate reviews

### 5. 🎯 Recommendation Service

- **Purpose**: Suggests similar movies or top-rated movies based on user preferences and feedback.
- **Key Features**:
    - Recommend movies based on genre and user feedback

## 🛠️ Build & Run Guide

This project can be run in two ways: using **Docker Compose** or **Maven**.

---

### ✅ 1. Running with Docker Compose

Recommended for launching all microservices at once.

#### 📦 Build & Run

```bash
docker compose up --build
```

> 🧠 **Don’t forget to create a `.env` file in the root directory with the following variables:**
> ```env
> RABBIT_BROKER_HOST=localhost
> RABBIT_BROKER_PORT=5672
> RABBIT_BROKER_USER=rabbit
> RABBIT_BROKER_PASSWORD=rabbit
> ```

#### 📊 Available Services

| Service                | Port  | Description                    |
|------------------------|-------|--------------------------------|
| Movie Service          | 8081  | Movie and genre management     |
| Film Person Service    | 8082  | Actors, directors, creators    |
| User Service           | 8083  | Users, login, roles            |
| Feedback Service       | 8084  | Ratings, reviews               |
| Recommendation Service | 8085  | Movie recommendations          |
| RabbitMQ Management UI | 15672 | http://localhost:15672         |

> **RabbitMQ login**: `rabbit` / `rabbit`

---

### ☕️ 2. Running with Maven (for local development)

If you want to run a single service manually, make sure RabbitMQ is running:

```bash
cd <service-name>
mvn spring-boot:run
```

Example:
```bash
cd user-service
mvn spring-boot:run
```
---

### 🖊️ API Documentation (OpenAPI / Swagger)

Once running, each service provides OpenAPI documentation at:

| Service                | URL                                   |
|------------------------|----------------------------------------|
| Movie Service          | http://localhost:8081/swagger-ui.html |
| Film Person Service    | http://localhost:8082/swagger-ui.html |
| User Service           | http://localhost:8083/swagger-ui.html |
| Feedback Service       | http://localhost:8084/swagger-ui.html |
| Recommendation Service | http://localhost:8085/swagger-ui.html |

---

## Use case
![Use Case](https://gitlab.fi.muni.cz/xtefr/movies-recommender-catalogue/-/wikis/uploads/2adf2376ab25e987a9962c24c976aa7a/useCase.png)


## Class diagram
![Class diagram](https://gitlab.fi.muni.cz/xtefr/movies-recommender-catalogue/-/wikis/uploads/04e1356a0d7d60760c59a327c0336254/classDiagram.png)

## AI Disclosure

During the development of this project, we utilized AI tools for coding assistance.