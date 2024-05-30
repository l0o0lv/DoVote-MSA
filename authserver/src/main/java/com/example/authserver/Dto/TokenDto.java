package com.example.authserver.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenDto {
    private Long keyId; // 유저 기본키 번호
    private String token;
    private String nickname;
    private String roles;
}
