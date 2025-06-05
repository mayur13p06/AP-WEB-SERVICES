package com.example.elearning.Services;

import com.example.elearning.DTO.AuthResponse;
import com.example.elearning.DTO.LoginRequest;
import com.example.elearning.DTO.SignupRequest;
import com.example.elearning.entity.User;
import com.example.elearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateToken(userDetails, 1000 * 60 * 15); // 15 min
        String refreshToken = jwtService.generateToken(userDetails, 1000 * 60 * 60 * 24 * 7); // 7 days

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        var userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String accessToken = jwtService.generateToken(userDetails, 1000 * 60 * 15); // 15 min
        String refreshToken = jwtService.generateToken(userDetails, 1000 * 60 * 60 * 24 * 7); // 7 days

        return new AuthResponse(accessToken, refreshToken);
    }
    public CustomUserDetails getUserDetails(String username) {
        return (CustomUserDetails) userDetailsService.loadUserByUsername(username);
    }

}
