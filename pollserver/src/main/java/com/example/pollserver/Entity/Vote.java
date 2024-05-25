package com.example.pollserver.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "polls_id",
                "user_id"
        }) // "poll_id"와 "user_id" 두 열의 조합이 고유
        // 같은 사용자가 같은 투표에 중복 투표를 할 수 없도록
})
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "polls_id", nullable = false)
    @JsonBackReference
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "choices_id", nullable = false)
    private Choice choice;



    private String nickname;
}

// 투표를 한 사용자의 정보를 데이터베이스에 저장하고 조회할 수 있는 엔티티 클래스로
// 여러 다른 엔티티(Poll, Choice, UserEntity)와 관계를 가지고 있음
// 어떤 사용자가 어떤 투표에 어떤 선택지로 투표했는지