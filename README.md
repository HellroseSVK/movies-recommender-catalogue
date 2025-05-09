# Movies Recommender Catalogue

## Description

The web application is a catalogue of movies of different genres.
Each movie has a title, description, film director, and list of actors, and image(s) from the main playbill.
Each movie is categorized in one or more genres.
Only the administrator can create, remove, and update the list of movies.
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

### 3. Feedback Service 📝

- **Purpose**: Handles both user ratings and written reviews for movies.
- **Key Features**:
    - Users can rate movies (0–100%) in different categories (acting, directing, etc.)
    - Users can submit written reviews along with ratings
    - Retrieve all feedback (ratings + reviews) for a movie
    - Calculate an overall movie rating
    - Retrieve user-specific feedback
    - Allow administrators to moderate reviews

### 4. 🎯 Recommendation Service

- **Purpose**: Suggests similar movies or top-rated movies based on user preferences and feedback.
- **Key Features**:
    - Recommend movies based on genre and user feedback

## Architecture Overview

### Persistence:

Each microservice uses its own relational database.
Data is managed through Spring Data JPA and stored in an H2 database for development and testing purposes.

### Asynchronous Communication:

Microservices communicate asynchronously via RabbitMQ as the message broker.
Events are published and consumed through message queues, enabling loose coupling and scalability across services.

The feedback and recommendation microservices use batch processing to process user ratings.

### Authentication:

The application uses OAuth2 for authentication and authorization.
Users are provided with a token upon successful login, which is required for accessing protected resources.
Users can get JWT token by using the OAuth2 client service, using MUNI credentials.

## 🛠️ Build & Run Guide

This project can be run in two ways: using **Docker Compose** or **Maven**.

---

### ✅ 1. Running with Docker Compose

Recommended for launching all microservices at once.

#### 📦 Build & Run

```bash
docker compose up --build
```

> 🧠 **You can optionally create a .env file in the root directory to override the default values below. These defaults
are used automatically unless a different setup is needed:**
> ```env
> RABBIT_BROKER_HOST=localhost
> RABBIT_BROKER_PORT=5672
> RABBIT_BROKER_USER=rabbit
> RABBIT_BROKER_PASSWORD=rabbit
> ```

#### 📊 Available Services

| Service                | Port  | Description                                                |
|------------------------|-------|------------------------------------------------------------|
| Graffana               | 3000  | [Graffana](http://localhost:3000/)                         |
| OAuth2 Client          | 8080  | [Oauth2 client for authentication](http://localhost:8080/) |
| Movie Service          | 8081  | [Movie and genre management](http://localhost:8081/)       |
| Film Person Service    | 8082  | [Actors, directors, creators](http://localhost:8082/)      |
| Feedback Service       | 8083  | [Ratings, reviews](http://localhost:8083/)                 |
| Recommendation Service | 8084  | [Movie recommendations](http://localhost:8084/)            |
| Prometheus             | 9090  | [Prometheus](http://localhost:9090/)                       |
| RabbitMQ               | 15672 | [RabbitMQ Management UI](http://localhost:15672)           |

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
|------------------------|---------------------------------------|
| Movie Service          | http://localhost:8081/swagger-ui.html |
| Film Person Service    | http://localhost:8082/swagger-ui.html |
| Feedback Service       | http://localhost:8083/swagger-ui.html |
| Recommendation Service | http://localhost:8084/swagger-ui.html |

---

## 🌱 Seeding Guide

### Setup

Before seeding the database, please create a bearer token with scope `test_write` and set it as an environment variable
`BEARER_TOKEN` in the terminal.

* Windows

```bash
$env:BEARER_TOKEN = "your_token_value"
```

* Linux

```bash
export BEARER_TOKEN="your_token_value"
```

For authentication use the OAuth2 Client,

To seed the databases with initial data, you can run `$ npm run seed` command in the root directory of the project or
root
directory of any service. Optionally you can also specify which service to seed from the root directory of the project
with following commands:

| Service             | URL                          |
|---------------------|------------------------------|
| All services        | `$ npm run seed`             |
| Movie Service       | `$ npm run seed:movie`       |
| Film Person Service | `$ npm run seed:film-person` |
| Feedback Service    | `$ npm run seed:feedback`    |

To specify the amount of seeded data in each service separately, you can use the following commands:

```shell
$ npm run seed:movie -- <number of entries>
```

This will not work when seeding all services at once.

You can either seed all services at one, or it is advised to seed the databases in the following order:

```
Film Person Service --> Movie Service --> Feedback Service 
```

---

## 🧪 Load Testing Scenarios
To verify the system's responsiveness and stability under typical user behavior, we implemented two Locust load testing scenarios. These scripts simulate realistic user flows across multiple microservices.

### 📦 Requirements

- Python 3.11+
- Locust installed (`pip install locust`)
- JWT decoding support (`pip install PyJWT`)

### 🚀 Running Locust
To start the load test interface:

```bash
locust -f locustfile.py
```
Then, open your web browser and navigate to `http://localhost:8089` to access the Locust web interface.

### 📈 Expected Results

- Identify which endpoints slow down under load
- Verify stability of asynchronous recommendation logic
- Ensure database queries (e.g., feedback by movie) return valid results under concurrency
- Test real-time response behavior across interconnected services

You can analyze performance metrics such as response time, throughput, and failure rates via the Locust web UI or export them for further profiling.

### 🎯 Scenario 1: User Interacts with Recommendations and Ratings

**Class:** `UserScenario`  
**Goal:** Simulate a logged-in user browsing recommended movies and submitting ratings.

#### 🧵 Flow

1. The test client loads a pre-generated JWT token from the `BEARER_TOKEN` environment variable.
2. It retrieves available rating categories from the Feedback Service.
3. It fetches recommended movies based on a selected category.
4. It submits a rating for one of the recommended movies, including a category-specific score and optional comment.

#### 🧩 Services Involved

- `RecommendationService` (port **8084**)
- `FeedbackService` (port **8083**)

#### ✅ Purpose

This scenario represents a typical user browsing and rating movies. It puts moderate load on both:
- the **Recommendation Service**, as recommendations are frequently fetched and parsed, and
- the **Feedback Service**, as it handles rating submission and category logic.


### 🎯 Scenario 2: User Views a Movie and Gets Similar Recommendations

**Class:** `MovieRecommendationUser`  
**Goal:** Simulate a user viewing details of a movie and exploring similar titles.

#### 🧵 Flow

1. The test client fetches a list of all movies from the Movie Service.
2. It selects a random movie and:
  - Retrieves the movie’s detail
  - Retrieves ratings for the movie
  - Retrieves a list of similar/recommended movies

#### 🧩 Services Involved

- `MovieService` (port **8081**)
- `FeedbackService` (port **8083**)
- `RecommendationService` (port **8084**)

#### ✅ Purpose

This scenario tests how the system performs when multiple related services are accessed in sequence — such as:
- Viewing movie metadata,
- Fetching aggregate or user-specific ratings, and
- Generating movie-based recommendations.


> 💡 **Note:** These scenarios assume the presence of seeded data and a valid bearer token. The system should be running (e.g., via Docker) before executing Locust tests.

---

## Use case

![Use Case](https://gitlab.fi.muni.cz/xtefr/movies-recommender-catalogue/-/wikis/uploads/2adf2376ab25e987a9962c24c976aa7a/useCase.png)

## Class diagram

![Class diagram](https://gitlab.fi.muni.cz/xtefr/movies-recommender-catalogue/-/wikis/uploads/04e1356a0d7d60760c59a327c0336254/classDiagram.png)

## AI Disclosure

During the development of this project, we utilized AI tools for coding assistance.
