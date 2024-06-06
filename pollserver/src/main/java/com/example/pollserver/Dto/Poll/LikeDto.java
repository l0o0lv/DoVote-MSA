package com.example.pollserver.Dto.Poll;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LikeDto {
    private Long userId;
    private Long pollId;
}
