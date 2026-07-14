package com.agritrace.module.user.service;

import com.agritrace.common.exception.BusinessException;
import com.agritrace.common.util.CodeGenerator;
import com.agritrace.module.user.dto.*;
import com.agritrace.module.user.entity.User;
import com.agritrace.module.user.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!user.getEnabled()) {
            throw new BusinessException("账户已被禁用");
        }

        // 生成简单令牌（生产环境使用 JWT）
        String token = CodeGenerator.generateSessionId();
        UserVO userVO = toUserVO(user);
        return new LoginResponse(token, userVO);
    }

    public UserVO register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Optional.ofNullable(request.getRole()).orElse("CONSUMER"));
        user = userRepository.save(user);
        return toUserVO(user);
    }

    public UserVO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return toUserVO(user);
    }

    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
