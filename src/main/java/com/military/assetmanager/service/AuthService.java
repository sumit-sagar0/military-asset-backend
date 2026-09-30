package com.military.assetmanager.service;
import com.military.assetmanager.dto.LoginRequest;
import com.military.assetmanager.dto.LoginResponse;
import com.military.assetmanager.model.User;
import com.military.assetmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
public class AuthService {
    @Autowired private UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Simple token: base64(userId:role:email)
        String tokenPayload = user.getId() + ":" + user.getRole().name() + ":" + user.getEmail();
        String token = Base64.getEncoder().encodeToString(tokenPayload.getBytes());

        Long baseId = user.getBase() != null ? user.getBase().getId() : null;
        return new LoginResponse(token, user.getRole().name(), user.getName(), baseId);
    }
}