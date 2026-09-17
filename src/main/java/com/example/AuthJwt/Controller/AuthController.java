package com.example.AuthJwt.Controller;

import com.example.AuthJwt.Dto.AuthResponse;
import com.example.AuthJwt.Dto.LoginRequest;
import com.example.AuthJwt.Dto.RegisterRequest;
import com.example.AuthJwt.Models.Role;
import com.example.AuthJwt.Models.Utilisateur;
import com.example.AuthJwt.Repository.UtilisateurRepository;
import com.example.AuthJwt.Service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Un compte existe déjà avec cet email.");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setRole(Role.USER);

        utilisateurRepository.save(utilisateur);

        String token = jwtService.generateToken(utilisateur.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, false));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
        );

        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow();

        if (utilisateur.isDeuxFAActive()) {
            // pas de JWT complet tant que le code 2FA n'est pas validé
            return ResponseEntity.ok(new AuthResponse(null, true));
        }

        String token = jwtService.generateToken(utilisateur.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, false));
    }
}
