package com.example.pollserver.Service;

import com.example.pollserver.Dto.Feign.PollResponseDto;
import com.example.pollserver.Dto.Poll.*;
import com.example.pollserver.Enum.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PollService {
    //모든 투표 조회
    List<PollResponse> getAllPolls();
    Optional<PollDto> getPollDtoById(Long id);
    // 투표 생성하기 (pollRequest로 받기)
    PollDto createPollWithChoices
    (PollRequest pollRequest, String title, String question,
     Category category);
    //투표 아이디로 삭제
    void deletePoll(Long id);
    // 프론트에서 받아온 title로 투표 조회하기
    List<PollResponse> getPollsByTitle(String title);
    // 카테고리별로 투표 조회하기
    List<PollResponse> getPollsByCategory(Category category);
    //투표 좋아요 기능
    void likePoll(LikeDto likeDto);
    //현재 로그인한 유저의 투표 생성 수 반환
    long getCreatedPollCount(String nickname);
    //현재 로그인한 유저의 투표 참여 수 반환
    long getParticipatedPollCount(String nickname);
    //대중성 포인트 반환
    Long getPopularPointByUsername(String username);
    //투표 종료하기
    void closePoll(ClosePollRequest closePollRequest);

    void awardPopularPoints(ClosePollRequest closePollRequest);

    PollResponseDto findById(Long userId);

    //회원 닉네임 수정 시 Poll db createdBy 필드 수정
//    void updatePollCreatedBy(UserDto userDto);
}