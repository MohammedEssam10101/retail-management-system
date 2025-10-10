package com.retail.management.service;

import com.retail.management.dto.request.auth.ChangePasswordRequest;
import com.retail.management.dto.request.auth.LoginRequest;
import com.retail.management.dto.request.auth.RegisterRequest;
import com.retail.management.dto.response.auth.LoginResponse;
import com.retail.management.dto.response.auth.TokenResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse register(RegisterRequest request);
    TokenResponse refreshToken(String refreshToken);
    void changePassword(ChangePasswordRequest request);
    void logout();
}