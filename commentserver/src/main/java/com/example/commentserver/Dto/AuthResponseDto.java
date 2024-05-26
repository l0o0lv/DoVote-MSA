package com.example.commentserver.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDto {
    private Long id;
    private String nickname;
    private Long popular_point;
    private String gender;
    private String age;
}
