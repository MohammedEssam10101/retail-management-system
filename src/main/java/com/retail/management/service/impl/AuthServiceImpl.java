package com.retail.management.service.impl;

import com.retail.management.config.JwtConfig;
import com.retail.management.dto.request.auth.ChangePasswordRequest;
import com.retail.management.dto.request.auth.LoginRequest;
import com.retail.management.dto.request.auth.RegisterRequest;
import com.retail.management.dto.response.auth.LoginResponse;
import com.retail.management.dto.response.auth.TokenResponse;
import com.retail.management.dto.response.user.UserResponse;
import com.retail.management.entity.Role;
import com.retail.management.entity.User;
import com.retail.management.exception.DuplicateResourceException;
import com.retail.management.exception.ResourceNotFoundException;
import com.retail.management.exception.UnauthorizedException;
import com.retail.management.mapper.UserMapper;
import com.retail.management.repository.BranchRepository;
import com.retail.management.repository.RoleRepository;
import com.retail.management.repository.UserRepository;
import com.retail.management.security.JwtTokenProvider;
import com.retail.management.security.SecurityUtils;
import com.retail.management.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BranchRepository branchRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, BranchRepository branchRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder, UserMapper userMapper, JwtConfig jwtConfig) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.branchRepository = branchRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtConfig = jwtConfig;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication.getName());

        // Get user details
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserResponse userResponse = userMapper.toResponse(user);

        log.info("User logged in successfully: {}", request.getUsername());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getExpiration())
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        log.info("Registration attempt for username: {}", request.getUsername());

        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Get role
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        // Create user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(role)
                .active(true)
                .build();

        // Set branch if provided
        if (request.getBranchId() != null) {
            user.setBranch(branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found")));
        }

        user = userRepository.save(user);

        log.info("User registered successfully: {}", user.getUsername());

        // Auto-login after registration
        LoginRequest loginRequest = LoginRequest.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        return login(loginRequest);
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        log.info("Token refresh attempt");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String roles = "ROLE_" + user.getRole().getName();
        String newAccessToken = jwtTokenProvider.generateTokenFromUsername(username, roles);

        log.info("Token refreshed for user: {}", username);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getExpiration())
                .build();
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed for user: {}", username);
    }

    @Override
    public void logout() {
        String username = SecurityUtils.getCurrentUsername().orElse("Unknown");
        SecurityContextHolder.clearContext();
        log.info("User logged out: {}", username);
    }
}