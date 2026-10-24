# ShopZone API

Spring Boot backend for **ShopZone**, a full-stack e-commerce platform.

## Overview

RESTful API for product catalog, authentication, shopping cart, orders, wishlist, and admin operations. Built as the server side of a complete online store with a separate React frontend.

## Tech Stack

- Java
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Maven

## Features

- Customer and admin registration / login with JWT
- Role-based access control (`CUSTOMER`, `ADMIN`)
- Product management with multi-image gallery
- Public product catalog (browse without login)
- Shopping cart and checkout
- Order creation and order history
- Admin order status updates
- Wishlist
- Search and filter by category / brand
- Product image upload and static file serving

## API Overview

| Area | Notes |
|------|--------|
| Auth | `/api/auth/login`, `/api/auth/register/**` |
| Products (read) | `GET /api/products/**` — public |
| Products (write) | create / update / delete — authenticated (admin) |
| Cart | `/api/cart/**` — authenticated |
| Orders | `/api/orders/**` — authenticated; admin routes under `/api/orders/admin/**` |
| Wishlist | `/api/wishlist/**` — authenticated |
| Uploads | `/uploads/**` — public static files |

Send JWT as:

```http
Authorization: Bearer <your_token>
```

## Project Structure

controller/   REST endpoints
service/      Business logic
repository/   JPA data access
model/        Entities
dto/          Request & response objects
mapper/       Entity ↔ DTO mapping
security/     JWT filter and utilities
config/       Security, CORS, web config

## Getting Started

Prerequisites

JDK 17 or newer
Maven
PostgreSQL running locally (or remote)

Database configuration
Update src/main/resources/application.properties (or application.yml):
spring.datasource.url=jdbc:postgresql://localhost:5432/YOUR_DATABASE
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
Replace placeholders with your local values. Do not commit real production secrets.

## Run the API

mvn spring-boot:run

Default base URL:
http://localhost:8080

## Quick test
1. Register a customer or admin via auth endpoints
2. Login and copy the JWT
3. Call protected endpoints with the Authorization header
4. Call GET /api/products/all (or your list endpoint) without a token to verify public catalog access

## Related repository
Frontend (React storefront & admin): shopzone-frontend

## Author
Ali Rahimi
