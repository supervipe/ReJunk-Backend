# ReJunk Backend

## 📌 Overview
The ReJunk backend is a RESTful API built using **Spring Boot** that powers the core functionality of the ReJunk platform. It handles user authentication, collection requests, item evaluation, marketplace listings, orders, and notifications.

The backend follows a **layered architecture**:
- Controller layer → handles HTTP requests  
- Service layer → contains business logic  
- Repository layer (JPA/Hibernate) → manages database access  
- PostgreSQL database → stores persistent data  

The application is containerized using **Docker** and can be run together with the database using **Docker Compose**, ensuring consistent environments across development and testing.

---

## ⚙️ Tech Stack
- Java 17  
- Spring Boot  
- Spring Data JPA (Hibernate)  
- Spring Security  
- PostgreSQL  
- Docker & Docker Compose  
- Maven  

---

## 🚀 How to Run the Backend

### ✅ Option 1: Using Docker (Recommended)

Make sure you have:
- Docker installed  
- Docker Compose installed  

Steps:
1. Clone the repository  
   git clone https://github.com/your-username/rejunk-backend.git  
   cd rejunk-backend  

2. Start the application  
   docker compose up --build  

The backend will be available at:  
http://localhost:8080  

---

### 💻 Option 2: Run Locally (Without Docker)

Make sure you have:
- Java 17  
- Maven  

Steps:
1. Build the project  
   ./mvnw clean install  

2. Run the application  
   ./mvnw spring-boot:run  

Note: You must have a PostgreSQL database running and configured in application.properties.

---

## 🧪 Running Tests

./mvnw test  

---

## 📦 Project Structure

src/main/java/com/rejunk/  
├── controller/     # REST endpoints  
├── service/        # Business logic  
├── repository/     # JPA repositories  
├── domain/         # Entities (User, Item, Order, etc.)  
└── config/         # Security and app configuration  

---

## 🔄 CI Pipeline

The project includes a GitHub Actions CI pipeline that:
- Builds the application  
- Runs unit tests  
- Builds the Docker image  
- Verifies the Docker Compose setup  

---

## ☁️ Future Deployment

The backend is designed to be deployed on AWS EC2 using Docker containers. The same Docker setup used locally can be reused in production, making deployment simple and consistent.
