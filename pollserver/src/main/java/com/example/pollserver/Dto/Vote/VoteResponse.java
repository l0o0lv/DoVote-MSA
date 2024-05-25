package com.example.pollserver.Dto.Vote;

import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponse {

    private Long pollId;

    private Long userId;

    private String nickname;



}
