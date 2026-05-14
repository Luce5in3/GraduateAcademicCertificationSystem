package com.graduate.certificate.controller;

import com.graduate.certificate.common.result.Result;
import com.graduate.certificate.dto.auth.ChangePasswordRequest;
import com.graduate.certificate.dto.auth.LoginRequest;
import com.graduate.certificate.dto.auth.LoginResponse;
import com.graduate.certificate.dto.auth.RegisterRequest;
import com.graduate.certificate.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success("登录成功", response);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success("注册成功");
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@Validated @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return Result.success("密码修改成功");
    }
}
