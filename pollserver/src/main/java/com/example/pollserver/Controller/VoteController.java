package com.example.pollserver.Controller;

import com.example.pollserver.Dto.Vote.VoteDto;
import com.example.pollserver.Dto.Vote.VoteResultDto;
import com.example.pollserver.Service.VoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/votes")
public class VoteController {

    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping //TODO : JWT 필터 필요
    public ResponseEntity<String> vote(@Valid @RequestBody VoteDto voteDto) {
            if (voteDto.getNickname() != null) {
                voteService.vote(voteDto);
                return ResponseEntity.status(HttpStatus.CREATED).body("투표가 성공적으로 등록되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 인증에 실패하였습니다.");
            }
    }

    //투표 완료한사람 정보 리턴
    @GetMapping("/ok/{userId}") //TODO : JWT 필터 필요
    public ResponseEntity<List<VoteDto>> getVotesByNickname(@PathVariable("userId") Long userId) {
        List<VoteDto> votes = voteService.getVotesByNickname(userId);
        return ResponseEntity.ok(votes);
    }

    @GetMapping("/selected-choices/{pollId}")
    public ResponseEntity<List<Map<String, Object>>> getSelectedOptionCounts(@PathVariable Long pollId) {
        List<Map<String, Object>> selectedOptionCounts = voteService.getSelectedOptionCounts(pollId);
        return ResponseEntity.status(HttpStatus.OK).body(selectedOptionCounts);
    }

    //현재 유저와 같은 선택지를 고른 유저의 닉네임 반환
    @GetMapping("/user-nicknames/{pollId}/{choiceId}/{userId}")
    public ResponseEntity<List<Long>> getUserNicknamesByVoteAndChoice(
            @PathVariable Long pollId,
            @PathVariable Long choiceId,
            @PathVariable Long userId) {

        List<Long> userNicknames = voteService.findUserNicknamesByVoteAndChoice(pollId, choiceId, userId);

        return new ResponseEntity<>(userNicknames, HttpStatus.OK);
    }

    //성별, 연령별 투표 결과
    @GetMapping("/results/{pollId}")
    public ResponseEntity<VoteResultDto> getVoteResults(@PathVariable Long pollId) {
        try {
            VoteResultDto voteResult = voteService.getVoteResultByPollId(pollId);
            return ResponseEntity.ok(voteResult);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}