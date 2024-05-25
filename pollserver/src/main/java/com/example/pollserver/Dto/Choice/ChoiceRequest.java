package com.example.pollserver.Dto.Choice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceRequest {

    private List<ChoiceDto> choiceDtos;
}
