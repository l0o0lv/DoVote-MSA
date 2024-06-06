package com.example.pollserver.Dto.Poll;

import com.example.pollserver.Entity.Like;
import com.example.pollserver.Entity.Poll;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeDto {
    private Long userId;
    private Long pollId;

    public static Like dtoToEntity(LikeDto likeDto) {
        Poll poll = Poll.builder().id(likeDto.getPollId()).build();

        return Like.builder()
                .userId(likeDto.getUserId())
                .poll(poll)
                .build();
    }
}
