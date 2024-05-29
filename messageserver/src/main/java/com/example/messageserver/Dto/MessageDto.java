package com.example.messageserver.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private Long messageId; // 쪽지 번호
    @JsonProperty("sender")
    private String senderNickname; // 쪽지 송신자
    @JsonProperty("receiver")
    private String receiverNickname; // 쪽지 수신자
    @NotBlank(message = "내용을 입력해 주세요")
    private String content; // 내용
    private LocalDateTime sendTime; // 송신 시간
    private boolean readStatus; // 읽음 여부
}
