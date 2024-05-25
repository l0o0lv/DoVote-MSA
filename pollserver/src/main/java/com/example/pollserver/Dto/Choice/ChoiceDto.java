package com.example.pollserver.Dto.Choice;

import com.example.pollserver.Entity.Choice;
import com.example.pollserver.Entity.Poll;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceDto {


    @Getter
    private Long id;


    @Size(max = 40, message = "최대 40자까지 입력 가능합니다.")
    private String text;

    private Long pollId;

    public static ChoiceDto entityToDto(Choice choice) {
        return new ChoiceDto(
                choice.getId(),
                choice.getText(),
                choice.getPoll().getId()

        );
    }

    public Choice dtoToEntity(){
        Poll poll = new Poll();
        poll.setId(pollId);
        return new Choice(id, text, poll);
    }


    @JsonIgnore
    public Long getPollId() {
        return pollId;
    }
}