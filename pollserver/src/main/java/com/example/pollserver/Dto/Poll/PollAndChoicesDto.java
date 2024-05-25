package com.example.pollserver.Dto.Poll;

import com.example.pollserver.Dto.Choice.ChoiceDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class PollAndChoicesDto {
    private PollDto pollDto;
    private List<ChoiceDto> choiceDtos;


}