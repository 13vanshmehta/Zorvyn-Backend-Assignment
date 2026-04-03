# Zorvyn Financial Backend System

Welcome to the Zorvyn Backend Assignment repository. This is a secure, production-grade Spring Boot backend designed for robust role-based access control, financial record management, and insightful data aggregations.

## Key Features

* **Advanced Role-Based Access Control (RBAC):** Built with Spring Security. Stateless JWT-based authentication resolving specific endpoint privileges entirely driven by `VIEWER`, `ANALYST`, and `ADMIN` hierarchical access.
* **Dual-API Exposure:** While core CRUD operations utilize standard REST principles, the intricate Dashboard aggregations (like complex monthly category breakdowns) have directly been exposed via **GraphQL** for modern front-end flexibility.
* **Solid Persistence:** Relational database mapping using Spring Data JPA, backed by native SQL capability for complex dashboard aggregate queries securely running under the hood on MySQL.
* **Extreme Validation:** A global interception layer catches missing variables and limits, and ensures an impenetrable standard of error reporting (`400`, `401`, `403`, `404`, `409`, `429`).
* **Rate Limiting:** Native anti-spam mechanisms utilizing Bucket4j.

---

## Tech Stack

- **Framework:** Java 21 + Spring Boot 3.2.x 
- **Security:** Spring Security + JSON Web Tokens (JJWT 0.12.6)
- **Database:** MySQL + Hibernate ORM
- **API Spec:** OpenAPI (Swagger 3) & Spring GraphQL
- **Protections:** Bucket4j (Rate Limiting), Jakarta Validation

---

## How to Setup and Run

### 1. Database Configuration
Ensure you have a local instance of MySQL running. Change the properties inside `src/main/resources/application.properties` to map your local connection:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/zorvyn_database
spring.datasource.username=root
spring.datasource.password=your_password
```
*(Hibernate `ddl-auto=update` is turned on, so there are no manual SQL table scripts to run. Simply ensure `zorvyn_database` schema exists).*

### 2. Startup Sequences
Run the application via Maven Wrapper:
```bash
./mvnw spring-boot:run
```
**Bootstrapping Notification:** The `DataSeeder.java` implements a `CommandLineRunner` that acts as a failsafe on boot-up. If the database is completely empty, it will instantly provision the mandatory ecosystem roles and one master administrator:
> **Email:** `admin@zorvyn.com`  
> **Password:** `Admin@123`

---

## API Documentation & Testing

This repository uses multiple avenues for API explanation:

1. **Swagger UI Interactor:** Visit `http://localhost:8080/swagger-ui/index.html` after booting the app. You can authorize your bearer tokens natively in the UI and test directly from your browser.
2. **GraphQL IDE:** The GraphQL endpoints map directly to `POST http://localhost:8080/api/v1/dashboard`. 
3. **The Recommended Path (Postman):** A detailed Step-by-Step Testing Flow documentation is available for reviewing and understanding all endpoint states seamlessly.

---

## Assumptions and Trade-Offs

**1. GraphQL vs REST on Aggregations:**
We purposefully made the assumption that a frontend client building a dynamic financial dashboard wants exactly the data they request without over-fetching. Because of this, the Dashboard analytical layer uses GraphQL (`/api/v1/dashboard`), while the high-velocity transactional boundaries (`/records`) sit in standard REST.

**2. Hard Deletes vs Soft Deletes:**
By default, the `DELETE /records/{id}` acts as a traditional hard wipe against the persistence layer. Soft deletes (`deleted=true`) could prevent data loss via mistaken auditing, but hard deletes were favoured here keeping compliance simplicity and storage overhead extremely low.

**3. In-Memory Rate Limiting**
The app utilizes Bucket4j for a highly scalable `ConcurrentHashMap` in-memory setup blocking users attempting to spike more than 30 HTTP queries per minute. In a horizontal multi-server infrastructure, we'd trade off this in-memory HashMap approach and switch to a centralized Redis architecture map using standard Lua scripts so limits are accurately persisted uniformly.
# Zorvyn-Backend-Assignment
