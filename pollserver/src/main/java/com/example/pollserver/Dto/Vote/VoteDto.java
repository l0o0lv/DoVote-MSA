package com.example.pollserver.Dto.Vote;


import com.example.pollserver.Dto.Feign.AuthResponseDto;
import com.example.pollserver.Entity.Vote;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoteDto {

    private Long id;

    @NotNull
    private Long choiceId;

    @NotNull
    private Long pollId;

    private String nickname;

    public VoteDto(Vote vote, AuthResponseDto authResponseDto) {
        this.id = vote.getId();
        this.choiceId = vote.getChoice().getId();
        this.pollId = vote.getPoll().getId();
        this.nickname = authResponseDto.getNickname();
    }


}