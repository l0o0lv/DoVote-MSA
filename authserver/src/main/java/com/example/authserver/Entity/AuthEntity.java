package com.example.authserver.Entity;

import com.example.authserver.Dto.AuthDto;
import com.example.authserver.Enum.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuthEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //회원번호

    private String phoneNum; //휴대폰 인증 위한 휴대폰번호
    private String uid; //아이디

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // JSON 결과로 출력하지 않을 데이터
    private String password; //비밀번호
    private String nickname; //닉네임
    private String gender; //성별
    private String age; //연령대
    private String mbti; //mbti
    private Long popular_point; //대중성 포인트
    private String firebaseToken;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> role = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "interest")
    private Set<Category> interests = new HashSet<>();



    @PrePersist // 엔티티를 저장 또는 업데이트할 때 실행되는 메서드
    public void prePersist() {
        if (this.popular_point == null)
            this.popular_point = 0L;
    }

    public static AuthEntity dtoToEntity(AuthDto authDto) {
        return AuthEntity.builder()
                .phoneNum(authDto.getPhoneNum())
                .uid(authDto.getUid())
                .password(authDto.getPassword())
                .nickname(authDto.getNickname())
                .gender(authDto.getGender())
                .age(authDto.getAge())
                .mbti(authDto.getMbti())
                .popular_point(authDto.getPopular_point())
                .firebaseToken(authDto.getFirebaseToken())
                .role(Collections.singletonList(authDto.getRole()))
                .interests(authDto.getInterests())
                .build();
    }

    public static AuthDto entityToDto(AuthEntity authEntity) {
        return AuthDto.builder()
                .id(authEntity.getId())
                .phoneNum(authEntity.getPhoneNum())
                .uid(authEntity.getUid())
                .password(authEntity.getPassword())
                .nickname(authEntity.getNickname())
                .gender(authEntity.getGender())
                .age(authEntity.getAge())
                .mbti(authEntity.getMbti())
                .popular_point(authEntity.getPopular_point())
                .firebaseToken(authEntity.getFirebaseToken())
                .role(authEntity.getRole().toString())
                .interests(authEntity.getInterests())
                .build();
    }
}
