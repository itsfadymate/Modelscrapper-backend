# ModelScrapper

ModelScrapper is a backend service that aggregates 3D printable models from popular websites, allowing users to search for STL files across multiple sources in one place. It was developed during my internship at a manufacturing company to streamline the process of finding 3D models, offering more features than alternatives like stlfinder.com.

## Index
- [Features](#features)
- [Supported Sources](#supported-sources)
- [API Endpoints](#api-endpoints)
- [Setup & Installation](#setup--installation)
  - [Prerequisites](#prerequisites)
  - [Configuration](#configuration)
  - [Running Locally](#running-locally)
  - [Running with Docker](#running-with-docker)
- [Tech Stack & Technologies (Backend)](#tech-stack--technologies-backend)
- [Frontend](#frontend)
- [Authentication & Security](#authentication--security)
- [Contribution](#contribution)
- [Contact](#contact)

## Features

- Unified search for 3D printable models across multiple major websites
- Download links for STL and related files
- Volume estimation for stl files
- Option to use custom search engine instead of the corresponding's website search engine
- Option to filter for free models only
- Slice STL files in the integrated kirimoto slicer
- View models in 3D
- Local file rehosting for more reliable downloads
- REST API endpoints for integration with any frontend
- Designed to be used with a [React + Vite frontend](https://github.com/itsfadymate/modelscrapper-frontend)

## Supported Sources

- Thingiverse
- Printables
- MyMiniFactory
- Cults3D
- Thangs
- GrabCAD
- Sketchfab

> **Note:**  
> Printables, GrabCAD, and Thangs may require periodic cookie updates for scraping. The other sources use public or authenticated APIs.

## API Endpoints

All endpoints are prefixed with `/api/models`.

| Endpoint                              | Method | Description                                                                                                               |
|----------------------------------------|--------|--------------------------------------------------------------------------------------------------------------------------|
| `/search`                             | GET    | Search for models. Parameters: `searchTerm`, `sources` (optional), `showFreeOnly` (optional), `sourcesToGoogle`(optional) |
| `/download`                           | GET    | Get download links for a model. Parameters: `sourceName`, `id`, `downloadPageUrl`                                         |
| `/download/localhostedlinks`          | GET    | Get download links  for resources rehosted temporarily on the local server  (mainly to bypass CORS issues)                |
| `/sources`                            | GET    | List all available sources                                                                                                |
| `/health`                             | GET    | Health check                                                                                                              |

### Example: Search Request

```
GET /api/models/search?searchTerm=robot&showFreeOnly=true
```

### Example: Download Request

```
GET /api/models/download?sourceName=thingiverse&id=12345&downloadPageUrl=https://www.thingiverse.com/thing:12345
```

## Setup & Installation

### Prerequisites

- Java 21
- Maven
- [Docker](https://www.docker.com/) (optional, for containerized deployment)
- [Node.js](https://nodejs.org/) (for running the frontend, if desired)

### Configuration

The application requires several environment variables, set in `application.properties`. These include:

- API keys/usernames for sources that require authentication (e.g., Cults3D)
- Cookie strings for Printables, GrabCAD, and Thangs (may need to be updated periodically)

**Example `application.properties`:**
```properties
# Thingiverse
thingiverse.api.url=https://api.thingiverse.com
thingiverse.api.token=your_api_token

# Cults3D
cults3D.api.username=your_username
cults3D.api.key=your_api_key
cults3D.cookies=your_cookie_string

# Printables
Printables.base.url=https://www.printables.com/
Printables.cookies=your_cookie_string

# GrabCAD
GrabCad.search.url=https://grabcad.com/library
GrabCad.cookies=your_cookie_string

```

>You can check out application.properties to know exactly what environmental variables need to be set up

### Running Locally

1. Clone the repository:
    ```
    git clone https://github.com/itsfadymate/Modelscrapper-backend.git
    cd ModelScrapper
    ```

2. Configure your `application.properties` as described above.

3. Build and run:
    ```
    mvn clean install
    mvn spring-boot:run
    ```

### Running with Docker

1. Build the Docker image:
    ```
    docker build -t modelscrapper .
    ```

2. Run the container, mounting your configuration:
    ```
    docker run -d -p 8080:8080 --env-file ./application.properties modelscrapper
    ```
    
## Tech Stack & Technologies (Backend)

- **Java 21**
- **Spring Boot** 
- **Spring Cloud OpenFeign** 
- **Microsoft Playwright**
- **JSoup** 
- **SLF4J**
- **Maven** 
- **Docker** 

## Frontend

A ready-to-use frontend is available at [itsfadymate/modelscrapper-frontend](https://github.com/itsfadymate/modelscrapper-frontend), built with React and Vite.

## Authentication & Security

- Some sources require API keys or cookies for access.
- No authentication is required for using the backend API itself.

## Contribution

Contributions are welcome! Please open issues or pull requests for bug fixes or new features.

## Contact

For questions or support, please open an issue or contact me @fadybassem263@gmail.com.

---
