package com.agritrace.module.user.controller;

import com.agritrace.common.response.ApiResult;
import com.agritrace.module.user.dto.*;
import com.agritrace.module.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResult.success(userService.login(request));
    }

    @PostMapping("/register")
    public ApiResult<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResult.success(userService.register(request));
    }
}

@RestController
@RequestMapping("/api/v1/users")
class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ApiResult<UserVO> getUser(@PathVariable Long id) {
        return ApiResult.success(userService.getUserById(id));
    }
}
