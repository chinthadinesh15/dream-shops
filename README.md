# Dream Shops - E-Commerce Application

**Dream Shops** is a robust backend for an e-commerce platform built with **Spring Boot**. It provides a comprehensive RESTful API to manage the entire shopping lifecycle, including user management, product cataloging, shopping cart functionality, and order processing.

---

## 🚀 Key Features

*   **User Management:** Registration, Login (JWT Authentication), and Profile Management.
*   **Product Catalog:** Categories, Products, Inventory Management, and Image Uploads.
*   **Shopping Cart:** Add/Remove items, Update quantities, and Calculate totals.
*   **Order System:** Place orders, View order history, and Order status tracking.
*   **Security:** Role-based access control (Admin vs. User) using Spring Security.

---

## 🛠 Technology Stack

*   **Framework:** Spring Boot 3.3.2
*   **Language:** Java 17
*   **Database:** MySQL
*   **ORM:** Spring Data JPA (Hibernate)
*   **Security:** Spring Security & JWT (JSON Web Tokens)
*   **Build Tool:** Maven
*   **Utilities:** Lombok, ModelMapper

---

## 🏗 Architecture

The project follows a standard layered architecture:

*   `controller`: Handles HTTP requests and responses.
*   `service`: Contains business logic.
*   `repository`: Interfaces for database interaction (Spring Data JPA).
*   `model`: Entity classes representing database tables.
*   `dto`: Data Transfer Objects for API communication.
*   `security`: JWT configuration and User Details services.
*   `exception`: Global exception handling.

---

## ⚙️ Setup & Installation

### Prerequisites
*   Java 17+ installed.
*   MySQL Server installed and running.
*   Maven installed (or use the provided `mvnw` wrapper).

### Configuration
1.  Clone the repository.
2.  Open `src/main/resources/application.properties`.
3.  Update database credentials:
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

## 🔗 API Reference

### Authentication
*   `POST /api/v1/auth/login`: Authenticate user and get JWT.

### Users
*   `POST /api/v1/users/add`: Register a new user.
*   `GET /api/v1/users/{userId}/user`: Get user details.
*   `PUT /api/v1/users/{userId}/update`: Update user profile.
*   `DELETE /api/v1/users/{userId}/delete`: Delete user account.

### Products
*   `GET /api/v1/products/all`: List all products.
*   `GET /api/v1/products/product/{id}/product`: Get product details.
*   `POST /api/v1/products/add`: Add new product (Admin).
*   `PUT /api/v1/products/product/{id}/update`: Update product (Admin).
*   `DELETE /api/v1/products/product/{id}/delete`: Delete product (Admin).

### Shopping Cart
*   `GET /api/v1/carts/{cartId}/my-cart`: View cart.
*   `POST /api/v1/cartItems/item/add`: Add item to cart.
*   `DELETE /api/v1/cartItems/cart/{cartId}/item/{itemId}/remove`: Remove item.
*   `PUT /api/v1/cartItems/cart/{cartId}/item/{itemId}/update`: Update item quantity.
*   `DELETE /api/v1/carts/{cartId}/clear`: Clear entire cart.

### Orders
*   `POST /api/v1/orders/order`: Place an order from current cart.
*   `GET /api/v1/orders/{orderId}/order`: Get order details.
*   `GET /api/v1/orders/user/{userId}/order`: Get user's order history.

---

## 🔮 Future Enhancements

*   Payment Gateway Integration (Stripe/PayPal).
*   Email Notifications for order confirmation.
*   Product Reviews and Ratings.
*   Advanced Search with Pagination and Sorting.

---

## 📝 License

This project is licensed under the Dinesh Babu License - see the LICENSE file for details.
