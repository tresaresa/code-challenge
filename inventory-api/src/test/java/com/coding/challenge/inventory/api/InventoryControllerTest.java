package com.coding.challenge.inventory.api;

import com.coding.challenge.inventory.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InventoryControllerTest extends BaseIntegrationTest {

    @Test
    void createInventory_success() throws Exception {
        Long categoryId = setupCategory("Clothes");
        Long subCategoryId = setupSubCategory("Shoe", categoryId);

        mockMvc.perform(post("/api/inventory/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inventoryJson("Running Shoe", "A running shoe", categoryId, subCategoryId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0));
    }

    @Test
    void createInventory_categoryNotFound() throws Exception {
        mockMvc.perform(post("/api/inventory/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inventoryJson("Item", "desc", 999L, 999L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(2));
    }

    @Test
    void createInventory_subCategoryNotFound() throws Exception {
        Long categoryId = setupCategory("Clothes");
        mockMvc.perform(post("/api/inventory/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inventoryJson("Item", "desc", categoryId, 999L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(2));
    }

    @Test
    void createInventory_subCategoryNotBelongToCategory() throws Exception {
        Long clothesId = setupCategory("Clothes");
        Long foodId = setupCategory("Food");
        Long shoeId = setupSubCategory("Shoe", clothesId);

        mockMvc.perform(post("/api/inventory/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inventoryJson("Item", "desc", foodId, shoeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(2));
    }

    @Test
    void createInventory_forbiddenCombination() throws Exception {
        Long foodId = setupCategory("Food");
        Long shoeId = setupSubCategory("Shoe", foodId);

        mockMvc.perform(post("/api/inventory/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inventoryJson("Shoe Item", "desc", foodId, shoeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(2));
    }

    @Test
    void createInventory_duplicated() throws Exception {
        Long categoryId = setupCategory("Clothes");
        Long subCategoryId = setupSubCategory("Shoe", categoryId);
        createInventory("Running Shoe", "desc", categoryId, subCategoryId);

        mockMvc.perform(post("/api/inventory/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inventoryJson("Running Shoe", "desc", categoryId, subCategoryId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(1));
    }

    @Test
    void updateQuantity_success() throws Exception {
        Long categoryId = setupCategory("Clothes");
        Long subCategoryId = setupSubCategory("Shoe", categoryId);
        createInventory("Running Shoe", "desc", categoryId, subCategoryId);
        Long inventoryId = getInventoryId("Running Shoe");

        mockMvc.perform(post("/api/inventory/update-quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inventory_id\":" + inventoryId + ",\"quantity\":100,\"user_id\":\"user1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0));
    }

    @Test
    void updateQuantity_inventoryNotFound() throws Exception {
        mockMvc.perform(post("/api/inventory/update-quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inventory_id\":999,\"quantity\":100,\"user_id\":\"user1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(2));
    }

    @Test
    void listAllInventories_empty() throws Exception {
        mockMvc.perform(get("/api/inventory/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0))
                .andExpect(jsonPath("$.inventories").isArray())
                .andExpect(jsonPath("$.inventories").isEmpty());
    }

    @Test
    void listAllInventories_withData() throws Exception {
        Long categoryId = setupCategory("Clothes");
        Long subCategoryId = setupSubCategory("Shoe", categoryId);
        createInventory("Running Shoe", "A running shoe", categoryId, subCategoryId);

        mockMvc.perform(get("/api/inventory/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0))
                .andExpect(jsonPath("$.inventories.length()").value(1))
                .andExpect(jsonPath("$.inventories[0].name").value("Running Shoe"))
                .andExpect(jsonPath("$.inventories[0].category").value("Clothes"))
                .andExpect(jsonPath("$.inventories[0].sub_category").value("Shoe"))
                .andExpect(jsonPath("$.inventories[0].quantity").value(0));
    }

    @Test
    void getInventory_success() throws Exception {
        Long categoryId = setupCategory("Clothes");
        Long subCategoryId = setupSubCategory("Shoe", categoryId);
        createInventory("Running Shoe", "desc", categoryId, subCategoryId);
        Long inventoryId = getInventoryId("Running Shoe");

        mockMvc.perform(get("/api/inventory/" + inventoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0))
                .andExpect(jsonPath("$.inventory.name").value("Running Shoe"))
                .andExpect(jsonPath("$.inventory.category").value("Clothes"));
    }

    @Test
    void getInventory_notFound() throws Exception {
        mockMvc.perform(get("/api/inventory/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(2));
    }

    @Test
    void deleteInventory_success() throws Exception {
        Long categoryId = setupCategory("Clothes");
        Long subCategoryId = setupSubCategory("Shoe", categoryId);
        createInventory("Running Shoe", "desc", categoryId, subCategoryId);
        Long inventoryId = getInventoryId("Running Shoe");

        mockMvc.perform(post("/api/inventory/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inventory_id\":" + inventoryId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0));
    }

    @Test
    void deleteInventory_notFound() throws Exception {
        mockMvc.perform(post("/api/inventory/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inventory_id\":999}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(2));
    }

    private Long setupCategory(String name) throws Exception {
        mockMvc.perform(post("/api/category/create-root")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"user_id\":\"user1\"}"))
                .andExpect(status().isOk());
        return jdbcTemplate.queryForObject(
                "SELECT id FROM categories WHERE name = ?", Long.class, name);
    }

    private Long setupSubCategory(String name, Long superCategoryId) throws Exception {
        mockMvc.perform(post("/api/category/create-sub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"super_category_id\":" + superCategoryId + ",\"user_id\":\"user1\"}"))
                .andExpect(status().isOk());
        return jdbcTemplate.queryForObject(
                "SELECT id FROM categories WHERE name = ?", Long.class, name);
    }

    private void createInventory(String name, String description, Long categoryId, Long subCategoryId) throws Exception {
        mockMvc.perform(post("/api/inventory/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inventoryJson(name, description, categoryId, subCategoryId)))
                .andExpect(status().isOk());
    }

    private String inventoryJson(String name, String description, Long categoryId, Long subCategoryId) {
        return "{\"name\":\"" + name + "\",\"description\":\"" + description
                + "\",\"category_id\":" + categoryId + ",\"sub_category_id\":" + subCategoryId
                + ",\"user_id\":\"user1\"}";
    }

    private Long getInventoryId(String name) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM inventories WHERE name = ?", Long.class, name);
    }
}