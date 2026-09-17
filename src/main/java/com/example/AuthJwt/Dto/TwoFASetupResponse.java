package com.example.AuthJwt.Dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TwoFASetupResponse {
    private String qrCodeBase64; // à afficher côté Angular en <img src="data:image/png;base64,...">
}
