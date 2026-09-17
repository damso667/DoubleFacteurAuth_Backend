package com.example.AuthJwt.Repository;

import com.example.AuthJwt.Models.CompteOAuth;
import com.example.AuthJwt.Models.ProviderOAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompteOAuthRepository extends JpaRepository<CompteOAuth,Long> {
    Optional<CompteOAuth> findByProviderAndProviderId(ProviderOAuth provider, String providerId);
}
