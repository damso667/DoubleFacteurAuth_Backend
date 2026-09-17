package com.example.AuthJwt.Controller;

import com.example.AuthJwt.Dto.*;
import com.example.AuthJwt.Models.Utilisateur;
import com.example.AuthJwt.Repository.UtilisateurRepository;
import com.example.AuthJwt.Service.JwtService;
import com.example.AuthJwt.Service.TotpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/2fa")
@RequiredArgsConstructor
public class TwoFAController {

    private final UtilisateurRepository utilisateurRepository;
    private final TotpService totpService;
    private final JwtService jwtService;

    // appelé par un utilisateur DÉJÀ connecté (JWT valide) qui veut activer le 2FA
    @PostMapping("/setup")
    public ResponseEntity<?> setup(Authentication authentication) {
        String email = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElseThrow();

        String secret = totpService.genererSecret();
        utilisateur.setSecret2FA(secret);
        // deuxFAActive reste false tant que le premier code n'est pas confirmé
        utilisateurRepository.save(utilisateur);

        String qrCodeBase64 = totpService.genererQrCodeBase64(email, secret);
        return ResponseEntity.ok(new TwoFASetupResponse(qrCodeBase64));
    }

    // confirme l'activation en vérifiant un premier code scanné
    @PostMapping("/activer")
    public ResponseEntity<?> activer(Authentication authentication, @RequestParam String code) {
        String email = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElseThrow();

        if (!totpService.verifierCode(utilisateur.getSecret2FA(), code)) {
            return ResponseEntity.badRequest().body("Code invalide.");
        }

        utilisateur.setDeuxFAActive(true);
        utilisateurRepository.save(utilisateur);
        return ResponseEntity.ok("2FA activé avec succès.");
    }

    // appelé APRÈS le login classique, quand deuxFARequise = true
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyTwoFARequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow();

        if (!totpService.verifierCode(utilisateur.getSecret2FA(), request.getCode())) {
            return ResponseEntity.badRequest().body("Code invalide.");
        }

        String token = jwtService.generateToken(utilisateur.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, false));
    }
}
