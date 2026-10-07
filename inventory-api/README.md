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
| POST | `/api/inventory/create` | Create inventory | `name`, `description`, `category_id`, `sub_category_id`, `user_id` | `OperationResponse` |
| POST | `/api/inventory/update-quantity` | Update quantity | `inventory_id`, `quantity`, `user_id` | `OperationResponse` |
| POST | `/api/inventory/delete` | Delete inventory | `inventory_id` | `OperationResponse` |
| GET | `/api/inventory/all` | List all inventories | — | `ListInventoriesResponse` (`inventories`) |
| GET | `/api/inventory/{id}` | Get inventory by id | — | `InventoryResponse` (`inventory`) |

### Category & Sub-Category

| Method | Path | Description | Request Body | Response |
|--------|------|-------------|--------------|----------|
| POST | `/api/category/create` | Create category | `category_name`, `user_id` | `OperationResponse` |
| POST | `/api/category/update` | Update category name | `category_id`, `name` | `OperationResponse` |
| POST | `/api/category/delete` | Delete category | `category_id` | `OperationResponse` |
| GET | `/api/category/all` | List all categories | — | `ListCategoriesResponse` (`categories`) |
| POST | `/api/category/create-sub` | Create sub-category | `sub_category_name`, `super_category_id`, `user_id` | `OperationResponse` |
| POST | `/api/category/update-sub` | Update sub-category name | `sub_category_id`, `name` | `OperationResponse` |
| POST | `/api/category/delete-sub` | Delete sub-category | `sub_category_id` | `OperationResponse` |
| GET | `/api/category/{category_id}` | List sub-categories of a category | — | `ListSubCategoriesResponse` (`sub_categories`) |

### Admin — User

| Method | Path | Description | Request Body | Response |
|--------|------|-------------|--------------|----------|
| POST | `/api/admin/user/create` | Create user | `user_id`, `display_name` | `OperationResponse` |
| POST | `/api/admin/user/update` | Update user | `user_id`, `display_name` | `OperationResponse` |
| POST | `/api/admin/user/delete` | Delete user | `user_id` | `OperationResponse` |
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
            sub_category_id → sub_categories.id, quantity CHECK>=0,
            create_user, create_timestamp)
users(id, user_id UNIQUE, display_name, create_timestamp)
```

Schema is initialized from `src/main/resources/schema.sql` on startup (`CREATE TABLE IF NOT EXISTS`).

## Assumptions

1. **Storage**: single-file SQLite, no external DB server required.
2. **User identity**: `create_user` on business tables is a free-form string; the `users` table is an independent admin-managed entity and is **not** foreign-keyed to business tables.
3. **Response convention**: business outcome is conveyed via `status_code` in the response body; HTTP status is always 200 (decouples business semantics from transport).
4. **Naming**: JSON fields use snake_case (Jackson `SNAKE_CASE`); Swagger UI schemas are kept in sync via `@JsonNaming`.
5. **Deletion**: deleting a category/sub-category with existing dependents is rejected (no cascade); the caller must delete dependents first.
6. **Quantity**: non-negative, enforced by a DB `CHECK` constraint and validated on update.
7. **Forbidden combinations**: configurable via `inventory.forbidden-combinations` in `application.properties` (format `SubCat:Cat,SubCat:Cat`).
8. **Timestamps**: stored as ISO-8601 text.

## Limitations

1. **No auth**: all endpoints are public; `AdminController` is not protected.
2. **No pagination**: list endpoints return full result sets.
3. **No transactions**: multi-step operations are not wrapped in `@Transactional`.
4. **DB is created separately**: schema uses `CREATE IF NOT EXISTS` instead of Flyway/Liquibase.
5. **No FK between `users` and business tables**: `create_user` remains a loose string.
6. **In-memory testing only**: tests use a file-based SQLite reinitialized per test; no production deployment configuration is included.