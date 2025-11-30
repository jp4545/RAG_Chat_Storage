package com.rag.chat.storage.controller;

import com.rag.chat.storage.dto.CreateUserRequest;
import com.rag.chat.storage.entity.UserEntity;
import com.rag.chat.storage.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "Users", description = "Manage Users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public UserEntity createUser(@RequestBody CreateUserRequest req) {
        return userService.createUser(req.name());
    }

    @Operation(
            summary = "Get All Users",
            description = "Get All Users"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users fethed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }
}