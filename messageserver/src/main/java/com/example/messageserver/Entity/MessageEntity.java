package com.example.messageserver.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity(name = "message")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId; // 쪽지 번호
    private Long senderId;
    private Long receiverId;
    private String content; // 내용

    @CreationTimestamp // 현재 시간 자동 삽입
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sendTime; // 송신 시간
    @ColumnDefault("FALSE")
    private boolean readStatus; // 읽음 여부

    @PrePersist
    protected void onCreate() {
        sendTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

}
