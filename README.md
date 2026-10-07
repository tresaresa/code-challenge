# Inventory API

A department store inventory management REST API built with Spring Boot. Supports managing inventories, categories, sub-categories, and users (admin).

## Tech Stack

- Spring Boot 4.1.1 
- Spring `JdbcTemplate` + SQLite DB
- Swagger UI
- JUnit 5 + MockMvc integration tests

## How to Run

```bash
./gradlew bootRun
```

Application starts on `http://localhost:8080`. Swagger UI at `http://localhost:8080/swagger-ui.html`.

## How to Test

```bash
./gradlew test
```

## API Reference

This service supports CRUD operation on inventory, category, and user. See below table for the API specifications.
All write operations return HTTP 200 with a business `status_code` in the body (`0`=success, `1`=duplicated, `2`=failed). JSON fields use **snake_case**.

### Inventory

| Method | Path | Description | Request Body | Response |
|--------|------|-------------|--------------|----------|
| POST | `/api/inventory/create` | Create inventory | `name`, `description`, `category_id`, `user_id` | `SimpleOperationResponse` |
| POST | `/api/inventory/update-quantity` | Update quantity | `inventory_id`, `quantity`, `user_id` | `SimpleOperationResponse` |
| POST | `/api/inventory/delete` | Delete inventory | `inventory_id` | `SimpleOperationResponse` |
| GET | `/api/inventory/all` | List all inventories | — | `ListInventoriesResponse` (`inventories`) |
| GET | `/api/inventory/{id}` | Get inventory by id | — | `InventoryResponse` (`inventory`) |

### Category & Sub-Category

| Method | Path | Description | Request Body | Response |
|--------|------|-------------|--------------|----------|
| POST | `/api/category/create` | Create category | `category_name`, `super_category_id`, `user_id` | `SimpleOperationResponse` |
| POST | `/api/category/update` | Update category name | `category_id`, `name` | `SimpleOperationResponse` |
| POST | `/api/category/delete` | Delete category | `category_id` | `SimpleOperationResponse` |
| GET | `/api/category/all` | List all categories | — | `ListCategoriesResponse` (`categories`) |
| GET | `/api/category/{category_id}` | List sub-categories of a category | — | `ListSubCategoriesResponse` (`sub_categories`) |

### Admin — User

| Method | Path | Description | Request Body | Response |
|--------|------|-------------|--------------|----------|
| POST | `/api/admin/user/create` | Create user | `user_id`, `display_name` | `SimpleOperationResponse` |
| POST | `/api/admin/user/update` | Update user | `user_id`, `display_name` | `SimpleOperationResponse` |
| POST | `/api/admin/user/delete` | Delete user | `user_id` | `SimpleOperationResponse` |
| GET | `/api/admin/user/all` | List all users | — | `ListUsersResponse` (`users`) |
| GET | `/api/admin/user/{user_id}` | Get user by user_id | — | `UserResponse` (`user`) |

### Response Envelope

All responses extend `BaseResponse`:

```json
{
  "status_code": 0,
  "error_message": "optional error detail",
  "...": "resource-specific field (e.g. inventories, user)"
}
```

`error_message` and null data fields are omitted from the JSON when absent.

## Database Schema

```sql
categories(id, name UNIQUE, create_user, create_timestamp)
sub_categories(id, name UNIQUE, super_category_id → categories.id, create_user, create_timestamp)
inventories(id, name UNIQUE, description, category_id → categories.id,
            quantity CHECK>=0,
            create_user, create_timestamp)
users(id, user_id UNIQUE, display_name, create_timestamp)
```

Schema is initialized from `src/main/resources/schema.sql`. Run DatabaseInitializerTest to create DB.

## Assumptions

1. **Storage**: single-file SQLite, no external DB server required.
2. **Authentication & Authorization**: Use user_id field of request body to identify the user. Authorization rule is permit all. This part could be extended later.
3. **Response convention**: business outcome is conveyed via `status_code` in the response body; HTTP status is always 200.
4. **Nested Category**: Nested categories are allowed. Categories with super_category_id=null are considered as root/top-level ones. No loop check and depth check but should have one later.
5. **User interface**: Only Swagger UI provided. Should have one GUI in the future. No pagination implemented.
