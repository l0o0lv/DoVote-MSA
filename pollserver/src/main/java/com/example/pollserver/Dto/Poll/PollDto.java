package com.example.pollserver.Dto.Poll;

import com.example.pollserver.Dto.Feign.AuthResponseDto;
import com.example.pollserver.Entity.Choice;
import com.example.pollserver.Entity.Poll;
import com.example.pollserver.Enum.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollDto {

    private Long pollId;
    private Long userId;
    private String createdBy;
    private String createdAt;

    @NotBlank(message = "제목을 입력해 주세요")
    @Size(max = 30)
    private String title;

    @NotBlank(message = "내용을 입력해 주세요.")
    @Size(max = 140)
    private String question;


    @Enumerated(EnumType.STRING)
    private Category category;  // 카테고리 필드 추가

    private List<Choice> choice;

    @Nullable
    private int likesCount = 0;

    private String mediaName;
    private String mediaUrl;

    public static PollDto entityToDto(Poll poll, AuthResponseDto authResponseDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return PollDto.builder()
                .pollId(poll.getId())
                .userId(poll.getUserId())
                .createdBy(authResponseDto.getNickname())
                .createdAt(poll.getCreatedAt().format(formatter))
                .title(poll.getTitle())
                .question(poll.getQuestion())
                .choice(poll.getChoices())
                .likesCount(poll.getLikesCount())
                .mediaName(poll.getMediaName())
                .mediaUrl(poll.getMediaUrl())
                .build();
    }

    public Poll dtoToEntity(PollDto pollDto) {
        return Poll.builder()
                .userId(pollDto.userId)
                .createdBy(pollDto.createdBy)
                .title(pollDto.title)
                .question(pollDto.question)
                .category(pollDto.category)
                .choices(pollDto.choice)
                .likesCount(pollDto.likesCount)
                .mediaName(pollDto.mediaName)
                .mediaUrl(pollDto.mediaUrl)
                .build();

    }


}


