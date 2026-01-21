package com.graduate.certificate.service;

import com.graduate.certificate.dto.auth.LoginRequest;
import com.graduate.certificate.dto.auth.LoginResponse;
import com.graduate.certificate.dto.auth.RegisterRequest;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户注册
     *
     * @param request 注册请求
     */
    void register(RegisterRequest request);
}
