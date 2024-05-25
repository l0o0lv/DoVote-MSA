package com.example.pollserver.Dto.Poll;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClosePollRequest {

    private Long pollId;
    private String nickname;

}

