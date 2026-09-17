package com.example.AuthJwt.Dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private boolean deuxFARequise;
}
