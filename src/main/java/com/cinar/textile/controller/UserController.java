package com.cinar.textile.controller;

import com.cinar.textile.dto.UserDto;
import com.cinar.textile.dto.request.ChangePasswordRequest;
import com.cinar.textile.dto.request.CreateUserRequest;
import com.cinar.textile.dto.response.UserResponse;
import com.cinar.textile.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/v1/api/user")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<UserResponse> getAllUsers(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                    @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        var userResponse = userService.getAllUsers(pageNo,pageSize);
        return ResponseEntity.ok(userResponse);
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        var user = userService.findUserByEmail(email);
        return ResponseEntity.ok(modelMapper.map(user,UserDto.class));
    }
    @PostMapping("/save")
    public ResponseEntity<UserDto> saveUser(@RequestBody CreateUserRequest createUserRequest) {
        var user = userService.createNewUser(createUserRequest);
        return ResponseEntity.ok(modelMapper.map(user,UserDto.class));
    }
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, Principal connectedUser) {
        userService.changePassword(changePasswordRequest,connectedUser);
        return ResponseEntity.ok().build();
    }
}
