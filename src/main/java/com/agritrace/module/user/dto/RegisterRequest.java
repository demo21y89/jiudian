package com.agritrace.module.user.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String nickname;
    private String role;
    private String phone;
    private String address;
}
