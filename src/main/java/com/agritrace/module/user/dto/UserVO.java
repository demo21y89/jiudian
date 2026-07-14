package com.agritrace.module.user.dto;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String role;
    private String phone;
    private String avatar;
    private String address;
}
