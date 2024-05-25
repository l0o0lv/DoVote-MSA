package com.example.pollserver.Dto.Vote;


import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class VoteResultDto {
    private Map<Long, Map<String, Integer>> choiceGenderCounts;
    private Map<Long, Map<String, Integer>> choiceAgeCounts;
    private Map<Long, String> choiceName;

    public VoteResultDto() {
        this.choiceGenderCounts = new HashMap<>();
        this.choiceAgeCounts = new HashMap<>();
        this.choiceName = new HashMap<>();
    }
}
