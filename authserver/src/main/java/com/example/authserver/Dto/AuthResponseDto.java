package com.example.authserver.Dto;

import com.example.authserver.Entity.AuthEntity;
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

    public static AuthResponseDto entityToDto(AuthEntity authEntity){
        return AuthResponseDto.builder()
                .id(authEntity.getId())
                .nickname(authEntity.getNickname())
                .popular_point(authEntity.getPopular_point())
                .gender(authEntity.getGender())
                .age(authEntity.getAge())
                .build();
    }
}
