package com.antifraude.dto;

import jakarta.validation.constraints.NotBlank;

public record DadosRegistro(
        @NotBlank(message = "O login é obrigatório")
        String login,

        @NotBlank(message = "A senha é obrigatória")
        String senha,

        String role
) {
}