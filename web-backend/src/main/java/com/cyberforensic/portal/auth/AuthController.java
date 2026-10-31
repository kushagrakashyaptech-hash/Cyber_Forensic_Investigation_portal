
package com.cyberforensic.portal.auth;

import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request,
            HttpSession session) {

        try {
            AuthService.AuthUser user = authService.register(
                    request.username(),
                    request.password(),
                    request.fullName(),
                    request.email(),
                    request.mobileNumber()
            );

            session.setAttribute("loggedInUser", user);

            return ResponseEntity.status(HttpStatus.CREATED).body(user);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpSession session) {

        try {
            AuthService.AuthUser user = authService.login(
                    request.identifier(),
                    request.password()
            );

            session.setAttribute("loggedInUser", user);

            return ResponseEntity.ok(user);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser(HttpSession session) {

        AuthService.AuthUser user =
                (AuthService.AuthUser) session.getAttribute("loggedInUser");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please log in first."));
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {

        session.invalidate();

        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully."));
    }

    public record RegisterRequest(
            String username,
            String password,
            String fullName,
            String email,
            String mobileNumber
    ) {}

    public record LoginRequest(
            String identifier,
            String password
    ) {}
}
