package org.apiimplementation.apiimplementations.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apiimplementation.apiimplementations.config.Message;
import org.apiimplementation.apiimplementations.error.customError.NotFoundException;
import org.apiimplementation.apiimplementations.model.User;
import org.apiimplementation.apiimplementations.security.pattern.IdPattern;
import org.apiimplementation.apiimplementations.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin(origins = "*") // Allow all origins
@RestController
@RequestMapping("/users")
@Tag(name = "Open API User Management", description = "Manage users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }




    // CREATE a new user
    @Operation(method = "POST", summary = "Create a new user", responses = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = Message.class)))
    })
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Message("User created successfully"));
    }




    // READ a user by ID
    @Operation(method = "GET", summary = "Get a user by ID", responses = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = Message.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "24-character alphanumeric User ID", name = "id",
                    schema = @Schema(implementation = IdPattern.class), in = ParameterIn.PATH,
                    example = "\"5f47a0d1a9c74d1e8bfa9e29\"", required = true)
            @PathVariable(name = "id") IdPattern userId
    ) {
        User user = userService.getUser(userId.getValue()).orElse(null);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            throw new NotFoundException("User with ID: " + userId + " not found");
        }
    }




    // READ all users
    @Operation(method = "GET", summary = "Get all users", responses = {
            @ApiResponse(responseCode = "200", description = "List of users",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class))))
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUserList();
        return ResponseEntity.ok(users);
    }




    // UPDATE a user by ID
    @Operation(method = "PUT", summary = "Update an existing user", responses = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = Message.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "24-character alphanumeric User ID", name = "id",
                    schema = @Schema(implementation = IdPattern.class), in = ParameterIn.PATH,
                    example = "\"5f47a0d1a9c74d1e8bfa9e29\"", required = true)
            @PathVariable(name = "id") IdPattern userId,
            @RequestBody User user
    ) {
        if (userService.getUser(userId.getValue()).isPresent()) {
            user.setId(userId.getValue()); // Ensure the correct ID is set
            userService.updateUser(user);
            return ResponseEntity.ok(new Message("User updated successfully"));
        } else {
            throw new NotFoundException("User with ID: " + userId + " not found");
        }
    }



    // DELETE a user by ID
    @Operation(method = "DELETE", summary = "Delete an existing user", responses = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = Message.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "24-character alphanumeric User ID", name = "id",
                    schema = @Schema(implementation = IdPattern.class), in = ParameterIn.PATH,
                    example = "\"5f47a0d1a9c74d1e8bfa9e29\"", required = true)
            @PathVariable(name = "id") IdPattern userId
    ) {
        if (userService.getUser(userId.getValue()).isPresent()) {
            userService.deleteUser(userId.getValue());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new Message("User deleted successfully"));
        } else {
            throw new NotFoundException("User with ID: " + userId + " not found");
        }
    }
}
