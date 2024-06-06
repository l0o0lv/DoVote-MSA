package com.example.pollserver.Dto.Poll;

import com.example.pollserver.Entity.PollLike;
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

    public static PollLike dtoToEntity(LikeDto likeDto) {
        Poll poll = Poll.builder().id(likeDto.getPollId()).build();

        return PollLike.builder()
                .userId(likeDto.getUserId())
                .poll(poll)
                .build();
    }
}
