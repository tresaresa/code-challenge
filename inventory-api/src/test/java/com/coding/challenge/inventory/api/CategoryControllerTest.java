package com.coding.challenge.inventory.api;

import com.coding.challenge.inventory.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerTest extends BaseIntegrationTest {

    @Test
    void createRootCategory_success() throws Exception {
        mockMvc.perform(post("/api/category/create-root")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Food\",\"user_id\":\"user1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0));
    }

    @Test
    void createRootCategory_duplicated() throws Exception {
        createRootCategory("Food");
        mockMvc.perform(post("/api/category/create-root")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Food\",\"user_id\":\"user1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(1));
    }

    @Test
    void createSubCategory_success() throws Exception {
        Long parentId = createRootCategory("Clothes");
        mockMvc.perform(post("/api/category/create-sub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Shoe\",\"super_category_id\":" + parentId + ",\"user_id\":\"user1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0));
    }

    @Test
    void createSubCategory_parentNotFound() throws Exception {
        mockMvc.perform(post("/api/category/create-sub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Shoe\",\"super_category_id\":999,\"user_id\":\"user1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(2));
    }

    @Test
    void createSubCategory_duplicated() throws Exception {
        Long parentId = createRootCategory("Clothes");
        createSubCategory("Shoe", parentId);
        mockMvc.perform(post("/api/category/create-sub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Shoe\",\"super_category_id\":" + parentId + ",\"user_id\":\"user1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(1));
    }

    @Test
    void listAllRootCategories_empty() throws Exception {
        mockMvc.perform(get("/api/category/all-root"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0))
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories").isEmpty());
    }

    @Test
    void listAllRootCategories_withData() throws Exception {
        createRootCategory("Food");
        createRootCategory("Clothes");
        mockMvc.perform(get("/api/category/all-root"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0))
                .andExpect(jsonPath("$.categories.length()").value(2));
    }

    @Test
    void listSubCategories_success() throws Exception {
        Long parentId = createRootCategory("Clothes");
        createSubCategory("Shoe", parentId);
        mockMvc.perform(get("/api/category/sub/" + parentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0))
                .andExpect(jsonPath("$.categories.length()").value(1))
                .andExpect(jsonPath("$.categories[0].name").value("Shoe"));
    }

    @Test
    void listSubCategories_parentNotFound() throws Exception {
        mockMvc.perform(get("/api/category/sub/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(2));
    }

    @Test
    void updateCategory_success() throws Exception {
        Long categoryId = createRootCategory("Food");
        mockMvc.perform(post("/api/category/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category_id\":" + categoryId + ",\"name\":\"Grocery\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0));
    }

    @Test
    void updateCategory_notFound() throws Exception {
        mockMvc.perform(post("/api/category/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category_id\":999,\"name\":\"X\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(2));
    }

    @Test
    void updateCategory_duplicated() throws Exception {
        createRootCategory("Food");
        Long clothesId = createRootCategory("Clothes");
        mockMvc.perform(post("/api/category/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category_id\":" + clothesId + ",\"name\":\"Food\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(1));
    }

    @Test
    void deleteRootCategory_success() throws Exception {
        Long categoryId = createRootCategory("Food");
        mockMvc.perform(post("/api/category/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category_id\":" + categoryId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0));
    }

    @Test
    void deleteRootCategory_hasChildren() throws Exception {
        Long parentId = createRootCategory("Clothes");
        createSubCategory("Shoe", parentId);
        mockMvc.perform(post("/api/category/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category_id\":" + parentId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(2));
    }

    @Test
    void deleteSubCategory_success() throws Exception {
        Long parentId = createRootCategory("Clothes");
        Long subId = createSubCategory("Shoe", parentId);
        mockMvc.perform(post("/api/category/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category_id\":" + subId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0));
    }

    @Test
    void deleteSubCategory_hasDependencies() throws Exception {
        Long parentId = createRootCategory("Clothes");
        Long subId = createSubCategory("Shoe", parentId);
        jdbcTemplate.update("INSERT INTO inventories (name, description, category_id, sub_category_id, quantity, create_user, create_timestamp) VALUES (?, ?, ?, ?, ?, ?, ?)",
                "Item", "desc", parentId, subId, 0, "user1", java.time.LocalDateTime.now().toString());
        mockMvc.perform(post("/api/category/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category_id\":" + subId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(2));
    }

    private Long createRootCategory(String name) throws Exception {
        mockMvc.perform(post("/api/category/create-root")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"user_id\":\"user1\"}"))
                .andExpect(status().isOk());
        return jdbcTemplate.queryForObject(
                "SELECT id FROM categories WHERE name = ?", Long.class, name);
    }

    private Long createSubCategory(String name, Long superCategoryId) throws Exception {
        mockMvc.perform(post("/api/category/create-sub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"super_category_id\":" + superCategoryId + ",\"user_id\":\"user1\"}"))
                .andExpect(status().isOk());
        return jdbcTemplate.queryForObject(
                "SELECT id FROM categories WHERE name = ?", Long.class, name);
    }
}