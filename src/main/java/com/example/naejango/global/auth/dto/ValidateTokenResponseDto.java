package com.example.naejango.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateTokenResponseDto {
    private Long userId;
    private boolean isValidToken;
}
