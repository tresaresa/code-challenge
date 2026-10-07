package com.coding.challenge.inventory.api;

import com.coding.challenge.inventory.dto.request.CreateUserRequest;
import com.coding.challenge.inventory.dto.request.DeleteUserRequest;
import com.coding.challenge.inventory.dto.request.UpdateUserRequest;
import com.coding.challenge.inventory.dto.response.ListUsersResponse;
import com.coding.challenge.inventory.dto.response.SimpleOperationResponse;
import com.coding.challenge.inventory.dto.response.UserResponse;
import com.coding.challenge.inventory.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/user")

@Tag(name = "Admin") 
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public SimpleOperationResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/all")
    public ListUsersResponse listAllUsers() {
        return userService.listAllUsers();
    }

    @GetMapping("/{user_id}")
    public UserResponse getUser(@PathVariable("user_id") String userId) {
        return userService.getUserByUserId(userId);
    }

    @PostMapping("/update")
    public SimpleOperationResponse updateUser(@Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(request);
    }

    @PostMapping("/delete")
    public SimpleOperationResponse deleteUser(@Valid @RequestBody DeleteUserRequest request) {
        return userService.deleteUser(request);
    }
}