# Dream Shops - E-Commerce Application Documentation

## 1. Project Overview
**Dream Shops** is a robust backend for an e-commerce platform built with **Spring Boot**. It provides a comprehensive RESTful API to manage the entire shopping lifecycle, including user management, product cataloging, shopping cart functionality, and order processing.

### Key Features
*   **User Management:** Registration, Login (JWT Authentication), and Profile Management.
*   **Product Catalog:** Categories, Products, Inventory Management, and Image Uploads.
*   **Shopping Cart:** Add/Remove items, Update quantities, and Calculate totals.
*   **Order System:** Place orders, View order history, and Order status tracking.
*   **Security:** Role-based access control (Admin vs. User) using Spring Security.

---

## 2. Technology Stack
*   **Framework:** Spring Boot 3.3.2
*   **Language:** Java 17
*   **Database:** MySQL
*   **ORM:** Spring Data JPA (Hibernate)
*   **Security:** Spring Security & JWT (JSON Web Tokens)
*   **Build Tool:** Maven
*   **Utilities:** Lombok, ModelMapper

---

## 3. Architecture & Project Structure
The project follows a standard layered architecture:

*   `controller`: Handles HTTP requests and responses.
*   `service`: Contains business logic.
*   `repository`: Interfaces for database interaction (Spring Data JPA).
*   `model`: Entity classes representing database tables.
*   `dto`: Data Transfer Objects for API communication.
*   `security`: JWT configuration and User Details services.
*   `exception`: Global exception handling.

---

## 4. Database Schema (Key Entities)

### 4.1 User
*   `id`: Long (PK)
*   `firstName`: String
*   `lastName`: String
*   `email`: String (Unique)
*   `password`: String
*   `roles`: Many-to-Many relationship with Role entity.

### 4.2 Product
*   `id`: Long (PK)
*   `name`: String
*   `brand`: String
*   `price`: BigDecimal
*   `inventory`: Integer
*   `description`: String
*   `category`: Many-to-One relationship with Category.
*   `images`: One-to-Many relationship with Image.

### 4.3 Cart & CartItem
*   **Cart**: Linked to a User (One-to-One). Contains a list of items and total amount.
*   **CartItem**: Links a Product to a Cart with a specific quantity and unit price.

### 4.4 Order & OrderItem
*   **Order**: Represents a finalized purchase. Linked to User. Contains order date, status, and total amount.
*   **OrderItem**: Snapshot of the product details at the time of purchase.

---

## 5. API Reference

### 5.1 Authentication
*   `POST /api/v1/auth/login`: Authenticate user and get JWT.

### 5.2 Users
*   `POST /api/v1/users/add`: Register a new user.
*   `GET /api/v1/users/{userId}/user`: Get user details.
*   `PUT /api/v1/users/{userId}/update`: Update user profile.
*   `DELETE /api/v1/users/{userId}/delete`: Delete user account.

### 5.3 Products (Public & Admin)
*   `GET /api/v1/products/all`: List all products.
*   `GET /api/v1/products/product/{id}/product`: Get product details.
*   `POST /api/v1/products/add`: Add new product (Admin).
*   `PUT /api/v1/products/product/{id}/update`: Update product (Admin).
*   `DELETE /api/v1/products/product/{id}/delete`: Delete product (Admin).
*   **Search/Filter**:
    *   `/products/by/brand-and-name`
    *   `/products/by/category-and-brand`
    *   `/products/{name}/products`

### 5.4 Categories
*   `GET /api/v1/categories/all`: List all categories.
*   `POST /api/v1/categories/add`: Add category.

### 5.5 Shopping Cart
*   `GET /api/v1/carts/{cartId}/my-cart`: View cart.
*   `POST /api/v1/cartItems/item/add`: Add item to cart.
*   `DELETE /api/v1/cartItems/cart/{cartId}/item/{itemId}/remove`: Remove item.
*   `PUT /api/v1/cartItems/cart/{cartId}/item/{itemId}/update`: Update item quantity.
*   `DELETE /api/v1/carts/{cartId}/clear`: Clear entire cart.

### 5.6 Orders
*   `POST /api/v1/orders/order`: Place an order from current cart.
*   `GET /api/v1/orders/{orderId}/order`: Get order details.
*   `GET /api/v1/orders/user/{userId}/order`: Get user's order history.

### 5.7 Images
*   `POST /api/v1/images/upload`: Upload product images.
*   `GET /api/v1/images/image/download/{imageId}`: Download image.

---

## 6. Setup & Installation

### Prerequisites
*   Java 17+ installed.
*   MySQL Server installed and running.
*   Maven installed (or use the provided `mvnw` wrapper).

### Configuration
1.  Open `src/main/resources/application.properties`.
2.  Update database credentials:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/dream_shops_db
    spring.datasource.username=YOUR_USERNAME
    spring.datasource.password=YOUR_PASSWORD
    ```

### Running the Application
1.  Open a terminal in the project root.
2.  Run the command:
    ```bash
    ./mvnw spring-boot:run
    ```
3.  The application will start on `http://localhost:9191`.

---

## 7. Error Handling
The application uses a global exception handler (`GlobalExceptionHandler`) to return consistent JSON error responses:
```json
{
    "message": "Error description",
    "data": null
}
```
Common status codes:
*   `200 OK`: Success.
*   `404 Not Found`: Resource not found.
*   `409 Conflict`: Duplicate entry (e.g., email already exists).
*   `500 Internal Server Error`: Server-side issues.

---

## 8. Future Enhancements
*   Payment Gateway Integration (Stripe/PayPal).
*   Email Notifications for order confirmation.
*   Product Reviews and Ratings.
*   Advanced Search with Pagination and Sorting.
