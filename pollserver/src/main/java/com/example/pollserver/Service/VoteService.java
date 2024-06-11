package com.example.pollserver.Service;

import com.example.pollserver.Dto.Vote.VoteDto;
import com.example.pollserver.Dto.Vote.VoteResultDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface VoteService {

    void vote(VoteDto voteDto);


    //닉네임으로 투표 결과 보내주기
    List<VoteDto> getVotesByNickname(Long userId);


    //현재 투표에 선택 항목들의 선택된 수 리턴
    List<Map<String, Object>> getSelectedOptionCounts(Long pollId);


    List<Long> findUserNicknamesByVoteAndChoice(Long pollId, Long choiceId, Long userId);


//    void updateVoteNickname(UserDto userDto);

    //연령별, 성별별 투표 결과 리턴
    VoteResultDto getVoteResultByPollId(Long pollId);

}