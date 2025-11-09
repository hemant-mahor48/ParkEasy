# ParkEasy
Smart Parking Management System - ParkEasy A simpler yet modern parking management platform that still uses trending technologies but with reduced complexity. Perfect for learning microservices!

🎯 Project Overview
A digital parking system where users can find parking spots, make reservations, pay online, and parking lot owners can manage their facilities. Think of it as "Uber for Parking Spots."
Why This Project?

Clear, understandable business logic
Real-world application everyone can relate to
Uses modern tech stack
Manageable scope for learning
Can be built incrementally


🏗️ Microservices (Simplified)
1. API Gateway

Tech: Spring Cloud Gateway
Purpose: Single entry point for all requests
What it does: Routes requests to the right service

2. User Service

Tech: Spring Boot + PostgreSQL + Redis
Features:

User registration & login
Profile management
JWT authentication
Password reset
Redis caching for sessions



3. Parking Lot Service

Tech: Spring Boot + PostgreSQL + MongoDB
Features:

Add/manage parking lots (for owners)
Parking spot details (car/bike, covered/open)
Pricing per hour
Opening/closing hours
Location (latitude, longitude)



4. Booking Service

Tech: Spring Boot + PostgreSQL + Kafka
Features:

Search available parking spots
Make reservations
Check-in/check-out
Booking history
Send booking events to Kafka



5. Payment Service

Tech: Spring Boot + PostgreSQL + Stripe API
Features:

Process payments
Calculate parking charges
Refund handling
Payment history
Integration with Stripe/Razorpay



6. Notification Service

Tech: Spring Boot + Kafka + Email/SMS
Features:

Listen to Kafka events
Send booking confirmations
Send payment receipts
Send reminders (parking expiry)
Email via SendGrid/AWS SES




🛠️ Technology Stack (Beginner-Friendly)
Backend

Java 17 with Spring Boot 3.2
Spring Data JPA: Database operations
Spring Security: Authentication
Spring Cloud Gateway: API Gateway
Spring Cloud Netflix Eureka: Service Discovery

Databases

PostgreSQL: Main database (Users, Bookings, Payments)
MongoDB: Parking lot details (flexible schema for different lot types)
Redis: Caching and session management

Message Queue

Apache Kafka: Async communication between services

Topics: booking-created, payment-completed, notification-events



Containerization

Docker: Package each service
Docker Compose: Run all services locally
Kubernetes (Optional): For production deployment

AWS Services (Basic)

EC2: Host services (or use ECS)
RDS: Managed PostgreSQL
S3: Store parking lot images
SES: Send emails
CloudWatch: Basic logging

Monitoring (Simple)

Spring Boot Actuator: Health checks
Prometheus: Collect metrics
Grafana: Simple dashboards
ELK Stack (Optional): Centralized logs

Testing

JUnit 5: Unit tests
Mockito: Mocking
Testcontainers: Integration tests with real databases
Postman/RestAssured: API testing

CI/CD (Simple)

GitHub Actions: Automated builds and tests
Docker Hub: Store Docker images


📊 How Services Communicate
Synchronous (REST APIs)
User → API Gateway → Parking Lot Service (search spots)
User → API Gateway → Booking Service (create booking)
Asynchronous (Kafka)
Booking Service → Kafka → Notification Service (send confirmation)
Payment Service → Kafka → Booking Service (update booking status)

🗂️ Simple Project Structure
parkeasy/
├── api-gateway/              # Routes requests
├── eureka-server/            # Service registry
├── user-service/             # User management
├── parking-lot-service/      # Parking spots
├── booking-service/          # Reservations
├── payment-service/          # Payments
├── notification-service/     # Emails/SMS
├── common-library/           # Shared code (DTOs, utils)
├── docker-compose.yml        # Run everything locally
└── README.md

🚀 Core Features (Keep It Simple)
For Users:

Search Parking: By location, date, time
View Details: Price, availability, photos
Book Spot: Reserve for specific time
Pay Online: Credit card, UPI
Get Confirmation: Email + SMS
Check-in/Check-out: QR code scan
View History: Past bookings

For Parking Lot Owners:

Register Lot: Add parking facility
Manage Spots: Add/remove spots
Set Pricing: Hourly rates
View Bookings: Current reservations
Earnings Dashboard: Revenue tracking

Admin Panel:

View all users
View all parking lots
Approve new parking lots
Handle disputes


📋 Database Schema (Simplified)
PostgreSQL Tables
users
sqlid, email, password, name, phone, role (USER/OWNER/ADMIN), created_at
parking_lots
sqlid, owner_id, name, address, latitude, longitude, 
total_spots, available_spots, price_per_hour, 
images (JSON), created_at
bookings
sqlid, user_id, parking_lot_id, spot_number,
start_time, end_time, status (PENDING/CONFIRMED/CANCELLED),
total_amount, created_at
payments
sqlid, booking_id, amount, payment_method, 
status (SUCCESS/FAILED), transaction_id, created_at

🎯 Step-by-Step Learning Path
Phase 1: Basics (Week 1-2)
Goal: Get one service running

Create User Service with Spring Boot
Setup PostgreSQL database
Implement REST APIs (register, login)
Add JWT authentication
Write basic unit tests
Run with Docker

What You'll Learn: Spring Boot basics, REST APIs, JWT, Docker

Phase 2: Multiple Services (Week 3-4)
Goal: Add more services and connect them

Create Parking Lot Service
Setup Eureka Server (service discovery)
Create API Gateway
Services talk to each other via REST
Add Redis caching

What You'll Learn: Microservices communication, Service Discovery, Caching

Phase 3: Async Communication (Week 5-6)
Goal: Add Kafka for events

Setup Kafka locally with Docker
Create Booking Service
Booking Service publishes events to Kafka
Create Notification Service (consumes Kafka events)
Send emails when booking is created

What You'll Learn: Event-driven architecture, Kafka, Async processing

Phase 4: Payment & Database (Week 7-8)
Goal: Add payment and use MongoDB

Create Payment Service
Integrate Stripe test API
Move parking lot details to MongoDB
Complete booking flow

What You'll Learn: External API integration, Multiple databases

Phase 5: Docker & Monitoring (Week 9-10)
Goal: Package everything

Dockerize all services
Create docker-compose.yml
Add Spring Boot Actuator
Setup Prometheus + Grafana
Add health checks

What You'll Learn: Docker Compose, Monitoring, Health checks

Phase 6: Cloud Deployment (Week 11-12)
Goal: Deploy to AWS

Setup AWS account
Deploy services to EC2 or ECS
Use RDS for PostgreSQL
Setup basic CI/CD with GitHub Actions
Add CloudWatch logging

What You'll Learn: Cloud deployment, CI/CD

🔧 Local Development Setup
Prerequisites

Java 17
Docker Desktop
IntelliJ IDEA / VS Code
Postman

Quick Start
bash# Clone repository
git clone https://github.com/yourusername/parkeasy

# Start infrastructure (databases, kafka)
docker-compose up -d postgres mongodb redis kafka

# Start Eureka Server
cd eureka-server && ./mvnw spring-boot:run

# Start API Gateway
cd api-gateway && ./mvnw spring-boot:run

# Start each service similarly
cd user-service && ./mvnw spring-boot:run
```

### **Access**
- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8080
- User Service: http://localhost:8081
- Parking Lot Service: http://localhost:8082

---

## 📝 Sample API Endpoints

### **User Service**
```
POST /api/v1/users/register
POST /api/v1/users/login
GET  /api/v1/users/profile
PUT  /api/v1/users/profile
```

### **Parking Lot Service**
```
POST /api/v1/parking-lots              (Owner creates)
GET  /api/v1/parking-lots/search       (Search by location)
GET  /api/v1/parking-lots/{id}         (Get details)
PUT  /api/v1/parking-lots/{id}         (Update)
```

### **Booking Service**
```
POST /api/v1/bookings                  (Create booking)
GET  /api/v1/bookings                  (User's bookings)
GET  /api/v1/bookings/{id}             (Booking details)
PUT  /api/v1/bookings/{id}/cancel      (Cancel booking)
```

### **Payment Service**
```
POST /api/v1/payments                  (Process payment)
GET  /api/v1/payments/{bookingId}      (Payment status)

🎨 Optional Enhancements (After Basics)
Once comfortable, you can add:

Search Optimization: Add Elasticsearch for better search
Real-time Updates: WebSocket for live availability
Mobile App: React Native app
QR Codes: Generate QR for check-in
Rating System: Users can rate parking lots
Analytics: Dashboard for owners
Kubernetes: Deploy on EKS
Load Testing: Use JMeter


🎓 What You'll Learn
✅ Spring Boot microservices
✅ REST API design
✅ PostgreSQL, MongoDB, Redis
✅ JWT Authentication
✅ Apache Kafka
✅ Docker & Docker Compose
✅ Service Discovery (Eureka)
✅ API Gateway
✅ Event-driven architecture
✅ Basic AWS deployment
✅ Monitoring with Prometheus
✅ CI/CD basics

📚 Learning Resources

Spring Boot: spring.io/guides
Kafka: kafka.apache.org/quickstart
Docker: docs.docker.com/get-started
PostgreSQL: postgresql.org/docs
AWS: aws.amazon.com/getting-started
