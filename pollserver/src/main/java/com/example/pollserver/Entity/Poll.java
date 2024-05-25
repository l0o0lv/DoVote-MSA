package com.example.pollserver.Entity;

import com.example.pollserver.Enum.Category;
import com.example.pollserver.Enum.VoteStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "polls") //엔티티가 매핑되는 데이터베이스 테이블의 이름 지정
@Builder
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 자동으로 생성
    private Long id;
    private Long userId;
    private String createdBy;
    @Column(name = "created_at")
    private String createdAt; //댓글 달때 달린 현재 시간 기능
    private String title;
    private String question;

    @Enumerated(EnumType.STRING)
    private Category category;  // 카테고리 필드 추가

    @OneToMany(
            // poll가 여러가지 choices 가질 수 있음
            cascade = CascadeType.PERSIST, //
            fetch = FetchType.EAGER, //Poll 검색시 Choices 즉시 로드
            orphanRemoval = true
    )
    @JoinColumn(name = "choices_id")
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 30)
    @JsonManagedReference
    private List<Choice> choices = new ArrayList<>(); //투표 선택지를 저장하는 리스트

    //투표가 받은 좋아요 수
    private int likesCount = 0;

    @Enumerated(EnumType.STRING)
    private VoteStatus voteStatus = VoteStatus.OPEN; // 투표 상태 추가

    @Column(name = "media_name")
    private String mediaName; //이미지, 동영상 파일 이름

    @Column(name = "media_url")
    private String mediaUrl; //미디어 데이터 저장 시 미디어 url을 이 컬럼에 저장

    @PrePersist //자동으로 날짜 및 시간을 설정하는 어노테이션
    protected void Createtime() {
        createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")); //현재시간을 저장
    }


}

// 데이터베이스에서 투표와 관련된 정보를 저장하고 관리
// (투표를 만든 사람과 업데이트한 사람에 관한 정보)