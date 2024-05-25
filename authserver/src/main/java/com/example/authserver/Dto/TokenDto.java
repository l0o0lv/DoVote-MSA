package com.example.authserver.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenDto {
    private String token;
    private String nickname;
    private String roles;
}
