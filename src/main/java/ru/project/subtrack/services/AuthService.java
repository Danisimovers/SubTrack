package ru.project.subtrack.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.project.subtrack.dto.AuthResponse;
import ru.project.subtrack.dto.LoginRequest;
import ru.project.subtrack.dto.RegisterRequest;
import ru.project.subtrack.models.User;
import ru.project.subtrack.repositories.UserRepository;
import ru.project.subtrack.security.JwtService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        validateEmailOrPhoneProvided(request.getEmail(), request.getPhoneNumber());
        checkUserUniqueness(request.getEmail(), request.getPhoneNumber());

        User user = User.builder()
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getUsername())
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail() != null ? user.getEmail() : user.getPhoneNumber()
        );

        return buildAuthResponse(user, token);
    }

    public AuthResponse login(LoginRequest request) {
        validateEmailOrPhoneProvided(request.getEmail(), request.getPhoneNumber());

        User user = userRepository.findByEmailOrPhoneNumber(request.getEmail(), request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("Invalid email/phone number or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email/phone number or password");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail() != null ? user.getEmail() : user.getPhoneNumber()
        );

        return buildAuthResponse(user, token);
    }

    // ---------- PRIVATE HELPERS ------------

    private void validateEmailOrPhoneProvided(String email, String phoneNumber) {
        if ((email == null || email.isBlank()) && (phoneNumber == null || phoneNumber.isBlank())) {
            throw new RuntimeException("Email or phone number must be provided");
        }
    }

    private void checkUserUniqueness(String email, String phoneNumber) {
        if (email != null && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already taken");
        }
        if (phoneNumber != null && userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new RuntimeException("Phone number already taken");
        }
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
