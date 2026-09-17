package com.example.AuthJwt.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyTwoFARequest {

    @NotBlank
    private String email;

    @NotBlank
    private String code;
}
