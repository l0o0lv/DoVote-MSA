package com.example.pollserver.Dto.Poll;

import com.example.pollserver.Dto.Feign.AuthResponseDto;
import com.example.pollserver.Entity.Choice;
import com.example.pollserver.Entity.Poll;
import com.example.pollserver.Enum.Category;
import com.example.pollserver.Enum.VoteStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PollResponse {

    private Long id;
    private Long userId;
    private String createdBy;
    private String createdAt;

    @NotBlank
    private String title;
    @NotBlank
    private String question;
    @Enumerated(EnumType.STRING)
    private Category category;  // 카테고리 필드 추가

    private List<Choice> choice;
    private int likesCount = 0;

    @Enumerated(EnumType.STRING)
    private VoteStatus voteStatus;
    private String mediaName;
    private String mediaUrl;


    public static PollResponse toDto(Poll poll, AuthResponseDto authResponseDto) {

        return new PollResponse(
                poll.getId(),
                poll.getUserId(),
                authResponseDto.getNickname(),
                poll.getCreatedAt(),
                poll.getTitle(),
                poll.getQuestion(),
                poll.getCategory(),
                poll.getChoices(),
                poll.getLikesCount(),
                poll.getVoteStatus(),
                poll.getMediaName(),
                poll.getMediaUrl()
        );
    }




}
