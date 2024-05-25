package com.example.pollserver.Service;

import com.example.pollserver.Dto.Choice.ChoiceRequest;

public interface ChoiceService {

    void saveChoices(Long pollId, ChoiceRequest choiceRequest);


}
