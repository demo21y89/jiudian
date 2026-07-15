package com.agritrace.module.user.controller;

import com.agritrace.common.response.ApiResult;
import com.agritrace.common.response.PageResult;
import com.agritrace.module.user.dto.*;
import com.agritrace.module.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResult<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse resp = userService.login(request);
        Map<String, Object> data = new HashMap<>();
        data.put("token", resp.getToken());
        data.put("user", resp.getUser());
        return ApiResult.success(data);
    }

    @PostMapping("/register")
    public ApiResult<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        UserVO user = userService.register(request);
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername(request.getUsername());
        loginReq.setPassword(request.getPassword());
        LoginResponse loginResp = userService.login(loginReq);
        Map<String, Object> data = new HashMap<>();
        data.put("token", loginResp.getToken());
        data.put("user", user);
        return ApiResult.success(data);
    }
}

@RestController
@RequestMapping("/api/v1/users")
class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResult<PageResult<List<UserVO>>> listUsers() {
        List<UserVO> users = userService.listAllUsers();
        return ApiResult.success(new PageResult<>(1, users.size(), users.size(), users));
    }

    @GetMapping("/{id}")
    public ApiResult<UserVO> getUser(@PathVariable Long id) {
        return ApiResult.success(userService.getUserById(id));
    }
}
