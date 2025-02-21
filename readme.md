# QuickCabs

## Project Overview

This repository houses the backend implementation for a ride-hailing application akin to Uber or Ola, built using the Spring Boot framework.  The project aims to deliver a robust and scalable solution encompassing key features like:

* Connecting riders and drivers
* Managing ride requests
* Calculating fares
* Handling payments

## Features

**User Management:**
* User registration and login (differentiated for Rider/Driver)
* User profile management
* Driver onboarding (captures vehicle information)

**Ride Management:**
* Riders can request rides (specifying pickup and drop-off locations)
* Drivers have the flexibility to accept or reject ride requests
* Real-time ride tracking
* Comprehensive ride history (maintained for both riders and drivers)

**Fare Calculation:**
*  Dynamic fare calculation engine (factors in distance, time, and potential surge pricing)

**Payment Processing:**
* Secure payment integration (example provided:  wallet or cash)
* Automated fare deduction from the rider's wallet
* Driver earnings management

**Rating and Review System:**
*  Post-ride rating system (riders and drivers can rate each other)
* Average ratings are prominently displayed on user profiles

## Technologies Used

* **Language:** Java
* **Framework:** Spring Boot
* **Database:** PostgreSQL with PostGIS extension (for location-based services)
* **ORM:** Spring Data JPA
* **Build Tool:** Maven
* **API Documentation:** SpringDoc OpenAPI (Swagger UI integrated)
* **Security:** Spring Security (JWT authentication)
* **Email:** Spring Mail
* **Testing:**  JUnit, Mockito, Spring Test, Testcontainers
* **Other Libraries:** Lombok (for boilerplate reduction), ModelMapper, JTS (spatial data handling), OSRM API Client (for distance calculations)

## Architecture

The project adheres to a layered architecture for maintainability and clarity:

* **Presentation Layer (Controllers):**  Responsible for handling HTTP requests, generating responses, and acting as an intermediary between the user interface and the service layer.
* **Service Layer:** Encapsulates business logic and orchestrates interactions between repositories and other services.
* **Repository Layer (Data Access):** Provides a clean abstraction layer for interacting with the database, abstracting away the complexities of data persistence.
* **Domain Model:** Represents the fundamental entities within the application's problem domain (e.g., users, rides, payments, ratings).
* **Strategy Pattern:** Employed strategically for achieving dynamic fare calculation and optimizing the driver matching process.

## Getting Started

**1. Prerequisites:**
* Java 21
* Maven
* Docker (optional, simplifies PostgreSQL setup within a container)

**2. Database Setup:**
* **Local Installation:** If you prefer a local setup, install PostgreSQL (ensure you include the PostGIS extension). Configure the connection details meticulously within your `application.properties` file.
* **Docker (Recommended):**  Leverage the provided `docker-compose.yaml` file to effortlessly spin up the database in a Docker container.

**3. Clone the Repository:**
   ```bash
   git clone https://github.com/your-username/uber-clone.git
   ```

**4. Build and Run:**
 Navigate to the project directory and execute the following command:
   ```bash
   mvn clean install spring-boot:run 
   ```
