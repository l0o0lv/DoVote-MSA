package com.example.authserver.Dto;

import com.example.authserver.Enum.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Builder
public class AuthDto {
    private Long id; //회원번호. 자동생성
    @NotBlank(message = "휴대폰 번호를 입력하세요")
    private String phoneNum;
    @NotBlank(message = "아이디를 입력하세요")
    private String uid; //아이디
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "비밀번호를 입력하세요")
    private String password; //비밀번호
    @NotBlank(message = "닉네임을 입력하세요")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요")
    private String nickname; //닉네임
    @NotBlank(message = "성별을 선택해주세요")
    private String gender; //성별
    private String age; //연령대
    @NotBlank(message = "MBTI를 선택해주세요")
    private String mbti; //mbti
    private Long popular_point; //대중성 포인트
    private String role;
    private String firebaseToken;

    private Set<Category> interests;


}