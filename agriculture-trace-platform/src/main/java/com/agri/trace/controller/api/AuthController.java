package com.agri.trace.controller.api;

import com.agri.trace.dto.LoginRequest;
import com.agri.trace.dto.LoginResponse;
import com.agri.trace.dto.R;
import com.agri.trace.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            return R.ok(authService.login(request));
        } catch (RuntimeException e) {
            return R.error(401, e.getMessage());
        }
    }

    @PostMapping("/register")
    public R<LoginResponse> register(@Valid @RequestBody LoginRequest request) {
        try {
            return R.ok(authService.register(request));
        } catch (RuntimeException e) {
            return R.error(400, e.getMessage());
        }
    }
}
