package com.example.pollserver.Controller;

import com.example.pollserver.Dto.Choice.ChoiceRequest;
import com.example.pollserver.Service.ChoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/choices")
public class ChoiceController {

    private static final Logger logger = LoggerFactory.getLogger(ChoiceController.class);
    private final ChoiceService choiceService;

    @Autowired
    public ChoiceController(ChoiceService choiceService) {
        this.choiceService = choiceService;
    }

    @PostMapping("/create/{pollId}")
    public ResponseEntity<String> createChoicesForPoll(
            @PathVariable Long pollId,
            @RequestBody ChoiceRequest choiceRequest) {


        choiceService.saveChoices(pollId, choiceRequest);
        logger.info("선택지 저장 완료 (Controller)");


        return new ResponseEntity<>("선택지가 성공적으로 생성되었습니다.", HttpStatus.CREATED);
    }
}
