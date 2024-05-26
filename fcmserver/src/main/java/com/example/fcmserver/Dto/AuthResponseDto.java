package com.example.fcmserver.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class AuthResponseDto {
    private Long id;
    private String nickname;
    private Long popular_point;
    private String gender;
    private String age;
    private String firebaseToken;
}
