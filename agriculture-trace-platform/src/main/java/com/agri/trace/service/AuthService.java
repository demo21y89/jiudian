package com.agri.trace.service;

import com.agri.trace.dto.LoginRequest;
import com.agri.trace.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse register(LoginRequest request);
}
