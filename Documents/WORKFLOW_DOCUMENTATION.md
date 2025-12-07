# Dream Shops Application Workflow Documentation

This document outlines the complete end-to-end workflow of the Dream Shops application, from user creation to placing an order.

## 1. User Registration & Authentication

Before shopping, a user needs an account.

### 1.1 Create a User
*   **Endpoint:** `POST /api/v1/users/add`
*   **Description:** Registers a new user in the system.
*   **Request Body:**
    ```json
    {
        "firstName": "Alice",
        "lastName": "Smith",
        "email": "alice@example.com",
        "password": "password123"
    }
    ```
*   **Response:** Returns the created user details.

### 1.2 Login
*   **Endpoint:** `POST /api/v1/auth/login`
*   **Description:** Authenticates the user and returns a JWT token for authorized access.
*   **Request Body:**
    ```json
    {
        "email": "alice@example.com",
        "password": "password123"
    }
    ```
*   **Response:**
    ```json
    {
        "message": "Login Successful",
        "data": {
            "id": 1,
            "token": "eyJhbGciOiJIUzI1NiJ9..."
        }
    }
    ```
    > **Note:** Use this `token` in the `Authorization` header (`Bearer <token>`) for subsequent requests if security is enabled.

---

## 2. Product Management (Admin Workflow)

Admins need to set up the catalog before users can shop.

### 2.1 Add a Category
*   **Endpoint:** `POST /api/v1/categories/add`
*   **Request Body:**
    ```json
    {
        "name": "Electronics"
    }
    ```

### 2.2 Add a Product
*   **Endpoint:** `POST /api/v1/products/add`
*   **Request Body:**
    ```json
    {
        "name": "Smartphone X",
        "brand": "TechBrand",
        "price": 699.99,
        "inventory": 50,
        "description": "Latest smartphone with high-end features.",
        "category": {
            "name": "Electronics"
        }
    }
    ```
*   **Response:** Returns the created product with its ID (e.g., `productId: 1`).

### 2.3 Upload Product Images
*   **Endpoint:** `POST /api/v1/images/upload?productId=1`
*   **Body:** `multipart/form-data` with key `files` containing image files.

---

## 3. Shopping Workflow (User)

Now the user can browse and shop.

### 3.1 Browse Products
*   **Endpoint:** `GET /api/v1/products/all`
*   **Description:** User views all available products.

### 3.2 Add Item to Cart
*   **Endpoint:** `POST /api/v1/cartItems/item/add?productId=1&quantity=1`
*   **Description:** Adds "Smartphone X" to the user's cart. The backend automatically links this to the authenticated user's cart (or creates one).
*   **Note:** This endpoint uses the logged-in user context to find/create the cart.

### 3.3 View Cart
*   **Endpoint:** `GET /api/v1/carts/{cartId}/my-cart`
*   **Description:** User checks their cart contents.
*   **Response:**
    ```json
    {
        "message": "Success",
        "data": {
            "id": 1,
            "items": [
                {
                    "itemId": 1,
                    "product": { "name": "Smartphone X", ... },
                    "quantity": 1,
                    "totalPrice": 699.99
                }
            ],
            "totalAmount": 699.99
        }
    }
    ```

### 3.4 Place Order
*   **Endpoint:** `POST /api/v1/orders/order?userId=1`
*   **Description:** Converts the items in the cart into a permanent order.
*   **Response:**
    ```json
    {
        "message": "Item Order Success!",
        "data": {
            "orderId": 1,
            "orderDate": "2023-10-27",
            "orderStatus": "PENDING",
            "totalAmount": 699.99,
            "items": [...]
        }
    }
    ```

### 3.5 View Order History
*   **Endpoint:** `GET /api/v1/orders/user/1/order`
*   **Description:** User can see their past orders.

---

## 4. Post-Order Management

### 4.1 Clear Cart
*   **Endpoint:** `DELETE /api/v1/carts/{cartId}/clear`
*   **Description:** (Optional) The cart is usually cleared automatically after placing an order, but this endpoint allows manual clearing.

### 4.2 Admin: Update Inventory/Product
*   **Endpoint:** `PUT /api/v1/products/product/1/update`
*   **Description:** Admin updates stock or details after sales.

---

## Summary of Flow
1.  **Register/Login** -> Get Token & User ID.
2.  **Admin** adds Categories & Products.
3.  **User** browses Products.
4.  **User** adds items to Cart (`POST /cartItems/item/add`).
5.  **User** places Order (`POST /orders/order`).
6.  **System** records Order and clears Cart.
