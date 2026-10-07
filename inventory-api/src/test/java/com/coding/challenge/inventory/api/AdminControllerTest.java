package com.coding.challenge.inventory.api;

import com.coding.challenge.inventory.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTest extends BaseIntegrationTest {

    @Test
    void createUser_success() throws Exception {
        mockMvc.perform(post("/api/admin/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":\"user1\",\"display_name\":\"Alice\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0));
    }

    @Test
    void createUser_duplicated() throws Exception {
        createUser("user1", "Alice");
        mockMvc.perform(post("/api/admin/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":\"user1\",\"display_name\":\"Bob\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(1));
    }

    @Test
    void createUser_nonAdminGetsUserRole() throws Exception {
        createUser("user1", "Alice");
        mockMvc.perform(get("/api/admin/user/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.role").value("USER"));
    }

    @Test
    void listAllUsers_empty() throws Exception {
        mockMvc.perform(get("/api/admin/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0))
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users.length()").value(1));
    }

    @Test
    void listAllUsers_withData() throws Exception {
        createUser("user1", "Alice");
        createUser("user2", "Bob");
        mockMvc.perform(get("/api/admin/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0))
                .andExpect(jsonPath("$.users.length()").value(3));
    }

    @Test
    void getUser_success() throws Exception {
        createUser("user1", "Alice");
        mockMvc.perform(get("/api/admin/user/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0))
                .andExpect(jsonPath("$.user.user_id").value("user1"))
                .andExpect(jsonPath("$.user.display_name").value("Alice"))
                .andExpect(jsonPath("$.user.role").value("USER"));
    }

    @Test
    void getUser_notFound() throws Exception {
        mockMvc.perform(get("/api/admin/user/unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(2));
    }

    @Test
    void updateUser_success() throws Exception {
        createUser("user1", "Alice");
        mockMvc.perform(post("/api/admin/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":\"user1\",\"display_name\":\"Alice Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0));
    }

    @Test
    void updateUser_notFound() throws Exception {
        mockMvc.perform(post("/api/admin/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":\"unknown\",\"display_name\":\"X\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(2));
    }

    @Test
    void deleteUser_success() throws Exception {
        createUser("user1", "Alice");
        mockMvc.perform(post("/api/admin/user/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":\"user1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(0));
    }

    @Test
    void deleteUser_notFound() throws Exception {
        mockMvc.perform(post("/api/admin/user/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":\"unknown\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(2));
    }

    private void createUser(String userId, String displayName) throws Exception {
        mockMvc.perform(post("/api/admin/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":\"" + userId + "\",\"display_name\":\"" + displayName + "\"}"))
                .andExpect(status().isOk());
    }
}