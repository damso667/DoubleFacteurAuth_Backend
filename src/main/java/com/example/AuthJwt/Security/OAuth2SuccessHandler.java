package com.example.AuthJwt.Security;

import com.example.AuthJwt.Models.*;
import com.example.AuthJwt.Repository.CompteOAuthRepository;
import com.example.AuthJwt.Repository.UtilisateurRepository;
import com.example.AuthJwt.Service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.AuthJwt.Models.ProviderOAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UtilisateurRepository utilisateurRepository;
    private final CompteOAuthRepository compteOAuthRepository;
    private final JwtService jwtService;

    @Value("${app.frontend.oauth-redirect}")
    private String frontendRedirectUrl;

    // Record ou classe interne pour transporter proprement les infos extraites du provider
    private record OAuth2UserInfo(String providerId, String email, String nom, String prenom) {}

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws java.io.IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        ProviderOAuth provider = ProviderOAuth.valueOf(registrationId.toUpperCase());

        // Extraction sous forme de Switch Expression
        OAuth2UserInfo userInfo = switch (provider) {
            case GOOGLE -> new OAuth2UserInfo(
                    (String) attributes.get("sub"),
                    (String) attributes.get("email"),
                    (String) attributes.getOrDefault("family_name", ""),
                    (String) attributes.getOrDefault("given_name", "")
            );
            case GITHUB -> {
                String pId = String.valueOf(attributes.get("id"));
                String mail = attributes.get("email") != null
                        ? (String) attributes.get("email")
                        : attributes.get("login") + "@users.noreply.github.com";
                String name = (String) attributes.getOrDefault("name", attributes.get("login"));
                yield new OAuth2UserInfo(pId, mail, name, "");
            }
            case LINKEDIN -> new OAuth2UserInfo(
                    (String) attributes.get("sub"),
                    (String) attributes.get("email"),
                    (String) attributes.getOrDefault("family_name", ""),
                    (String) attributes.getOrDefault("given_name", "")
            );
        };
        Utilisateur utilisateur = compteOAuthRepository.findByProviderAndProviderId(provider, userInfo.providerId())
                .map(CompteOAuth::getUtilisateur)
                .orElseGet(() -> {
                    Utilisateur u = utilisateurRepository.findByEmail(userInfo.email())
                            .orElseGet(() -> {
                                Utilisateur nouveau = new Utilisateur();
                                nouveau.setEmail(userInfo.email());
                                nouveau.setNom(userInfo.nom());
                                nouveau.setPrenom(userInfo.prenom());
                                nouveau.setRole(Role.USER);
                                return utilisateurRepository.save(nouveau);
                            });

                    CompteOAuth compteOAuth = new CompteOAuth();
                    compteOAuth.setProvider(provider);
                    compteOAuth.setProviderId(userInfo.providerId());
                    compteOAuth.setUtilisateur(u);
                    compteOAuthRepository.save(compteOAuth);

                    return u;
                });

        String token = jwtService.generateToken(utilisateur.getEmail());
        getRedirectStrategy().sendRedirect(request, response, frontendRedirectUrl + "?token=" + token);
    }
}
