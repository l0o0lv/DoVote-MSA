package com.example.pollserver.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "choices")
public class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "polls_id", nullable = false) //외래키 관계 설정
    // polls_id 컬럼을 사용하여 Choice 엔티티와 Poll 엔티티를 연결
    private Poll poll;

    public Choice(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Choice choice = (Choice) o;
        return Objects.equals(id, choice.id);
    }
    //동등성 비교

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

// 선택지와 관련된 정보를 데이터베이스에 저장하고 조회할 수 있는 엔티티 클래스
// 각 선택지의 텍스트 내용과 해당 선택지가 어떤 투표(Poll)와 관련있는지 관계를 표현하고 선택지의 텍스트 내용을 저장
//