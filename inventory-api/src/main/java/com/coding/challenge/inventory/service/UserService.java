package com.coding.challenge.inventory.service;

import com.coding.challenge.inventory.domain.User;
import com.coding.challenge.inventory.dto.UserView;
import com.coding.challenge.inventory.dto.request.CreateUserRequest;
import com.coding.challenge.inventory.dto.request.DeleteUserRequest;
import com.coding.challenge.inventory.dto.request.UpdateUserRequest;
import com.coding.challenge.inventory.dto.response.BaseResponse;
import com.coding.challenge.inventory.dto.response.ListUsersResponse;
import com.coding.challenge.inventory.dto.response.SimpleOperationResponse;
import com.coding.challenge.inventory.dto.response.UserResponse;
import com.coding.challenge.inventory.repository.UserRepository;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public SimpleOperationResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUserId(request.userId())) {
            return new SimpleOperationResponse(BaseResponse.DUPLICATED, "user id duplicated");
        }
        try {
            String role = "admin".equals(request.userId()) ? "ADMIN" : "USER"; // hardcode
            userRepository.insert(request.userId(), request.displayName(), role, LocalDateTime.now());
            return new SimpleOperationResponse(BaseResponse.SUCCESS);
        } catch (DataAccessException e) {
            return new SimpleOperationResponse(BaseResponse.FAILED, "database error");
        }
    }

    public ListUsersResponse listAllUsers() {
        List<UserView> views = userRepository.findAll().stream()
                .map(this::toView)
                .toList();
        return new ListUsersResponse(views);
    }

    public UserResponse getUserByUserId(String userId) {
        try {
            return userRepository.findByUserId(userId)
                    .map(u -> new UserResponse(toView(u)))
                    .orElseGet(() -> new UserResponse(BaseResponse.FAILED, "user not found"));
        } catch (DataAccessException e) {
            return new UserResponse(BaseResponse.FAILED, "database error");
        }
    }

    public SimpleOperationResponse updateUser(UpdateUserRequest request) {
        try {
            User user = userRepository.findByUserId(request.userId()).orElse(null);
            if (user == null) {
                return new SimpleOperationResponse(BaseResponse.FAILED, "user not found");
            }
            boolean updated = userRepository.update(user.id(), request.displayName());
            if (updated) {
                return new SimpleOperationResponse(BaseResponse.SUCCESS);
            }
            return new SimpleOperationResponse(BaseResponse.FAILED, "update failed");
        } catch (DataAccessException e) {
            return new SimpleOperationResponse(BaseResponse.FAILED, "database error");
        }
    }

    public SimpleOperationResponse deleteUser(DeleteUserRequest request) {
        try {
            User user = userRepository.findByUserId(request.userId()).orElse(null);
            if (user == null) {
                return new SimpleOperationResponse(BaseResponse.FAILED, "user not found");
            }
            boolean deleted = userRepository.deleteById(user.id());
            if (deleted) {
                return new SimpleOperationResponse(BaseResponse.SUCCESS);
            }
            return new SimpleOperationResponse(BaseResponse.FAILED, "delete failed");
        } catch (DataAccessException e) {
            return new SimpleOperationResponse(BaseResponse.FAILED, "database error");
        }
    }

    private UserView toView(User user) {
        return new UserView(user.id(), user.userId(), user.displayName(), user.role());
    }
}