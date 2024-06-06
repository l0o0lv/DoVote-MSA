package com.example.pollserver.Service;

import com.example.pollserver.Dto.Feign.AuthResponseDto;
import com.example.pollserver.Dto.Feign.PollResponseDto;
import com.example.pollserver.Dto.Poll.*;
import com.example.pollserver.Entity.Like;
import com.example.pollserver.Entity.Poll;
import com.example.pollserver.Entity.Vote;
import com.example.pollserver.Enum.Category;
import com.example.pollserver.Enum.VoteStatus;
import com.example.pollserver.Repository.LikeRepository;
import com.example.pollserver.Repository.PollRepository;
import com.example.pollserver.Repository.VoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.pollserver.Dto.Poll.LikeDto.*;
import static com.example.pollserver.Dto.Poll.PollDto.entityToDto;

@Service
@Slf4j
public class PollServiceImpl implements PollService{

    private static final Logger logger = LoggerFactory.getLogger(PollServiceImpl.class);
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final LikeRepository likeRepository;
    private final AuthFeignClient authFeignClient;

    @Autowired
    public PollServiceImpl(PollRepository pollRepository,
                           VoteRepository voteRepository,
                           LikeRepository likeRepository,
                           AuthFeignClient authFeignClient) {
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
        this.likeRepository = likeRepository;
        this.authFeignClient = authFeignClient;

    }


    //모든 투표 데이터 조회(likesCount 순으로 내림차순)
    public List<PollResponse> getAllPolls() {
        List<Poll> polls = pollRepository.findAll(Sort.by(Sort.Direction.DESC, "likesCount"));
        return polls.stream()
                .map(poll -> {
                    AuthResponseDto authResponseDto = authFeignClient.findById(poll.getUserId());
                    return PollResponse.toDto(poll, authResponseDto);
                })
                .collect(Collectors.toList());
    }


    //카테고리별 투표 조회
    public List<PollResponse> getPollsByCategory(Category category) {
        List<Poll> polls = pollRepository.findByCategory(category);
        return polls.stream()
                .map(poll -> {
                    AuthResponseDto authResponseDto = authFeignClient.findById(poll.getUserId());
                    return PollResponse.toDto(poll, authResponseDto);
                })
                .collect(Collectors.toList());
    }


    //투표 검색 이름으로 찾기
    public List<PollResponse> getPollsByTitle(String title) {
        List<Poll> polls = pollRepository.findByTitleContaining(title);
        return polls.stream()
                .map(poll -> {
                    AuthResponseDto authResponseDto = authFeignClient.findById(poll.getUserId());
                    return PollResponse.toDto(poll, authResponseDto);
                })
                .collect(Collectors.toList());
    }


    //투표 아이디로 투표 데이터 조회
    public Optional<PollDto> getPollDtoById(Long id) {
        Optional<Poll> poll = pollRepository.findById(id);
        return poll.map(p -> {
            AuthResponseDto authResponseDto = authFeignClient.findById(p.getUserId());
            return PollDto.entityToDto(p, authResponseDto);
        });
    }

    //투표 생성 (수정)
    public PollDto createPollWithChoices
            (PollRequest pollRequest, String title, String question,
             Category category) {
        log.info("createPollWithChoices 실행");

        AuthResponseDto findId = authFeignClient.findById(pollRequest.getUserId());

        PollDto pollDto = new PollDto();

        pollDto.setUserId(findId.getId());
        pollDto.setCreatedBy(pollRequest.getCreatedBy());
        pollDto.setTitle(title);
        pollDto.setQuestion(question);
        pollDto.setCategory(category);
        
        if (pollRequest.getMediaName() != null) {
            pollDto.setMediaName(pollRequest.getMediaName()); //미디어 이름 설정
        }
        if (pollRequest.getMediaUrl() != null) {
            pollDto.setMediaUrl(pollRequest.getMediaUrl()); //미디어 URL 설정
        }
        // 투표 정보 저장
        Poll createdPoll = pollRepository.save(pollDto.dtoToEntity(pollDto));

        logger.info("선택지 제외 투표 정보 저장 완료!");
        return entityToDto(createdPoll,findId);
    }

    //투표 삭제
    @Transactional
    public void deletePoll(Long id) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("투표를 찾을 수 없습니다."));

        //TODO : 해당 투표에 달린 댓글 삭제

        pollRepository.delete(poll);
    }


    // 투표 좋아요 기능
    public void likePoll(LikeDto likeDto) throws AccessDeniedException {
        //프론트에서 보내준 pollid에 맞는 투표 db에 접근 -> poll에 넣어줌
        Poll poll = pollRepository.findById(likeDto.getPollId())
                .orElseThrow(() -> new RuntimeException("투표를 찾을 수 없습니다."));

        if (likeRepository.existsByUserIdAndPollId(likeDto.getUserId(),likeDto.getPollId()))
            throw new AccessDeniedException("이미 좋아요를 눌렀습니다.");

        Like like = dtoToEntity(likeDto);
        likeRepository.save(like);
        poll.setLikesCount(poll.getLikesCount()+1);
        pollRepository.save(poll);
    }

    //현재 로그인한 유저의 생성한 투표수 반환
    public long getCreatedPollCount(String nickname) {
        logger.info("생성한 투표수를 반환 합니다. ");
        AuthResponseDto authResponseDto = authFeignClient.findByNickname(nickname);
        return pollRepository.countByUserId(authResponseDto.getId());
    }


    //현재 로그인한 유저의 참가한 투표수 반환
    public long getParticipatedPollCount(String nickname) {
        logger.info("참여한 투표수를 반환 합니다. ");
        AuthResponseDto authResponseDto = authFeignClient.findByNickname(nickname);
        return voteRepository.countByUserId(authResponseDto.getId());
    }

    //대중성 포인트 리턴
    public Long getPopularPointByUsername(String username) {
        AuthResponseDto authResponseDto = authFeignClient.findByNickname(username);
        return authResponseDto.getPopular_point();
    }

    public void closePoll(ClosePollRequest closePollRequest) {
        Poll poll = pollRepository.findById(closePollRequest.getPollId())
                .orElseThrow(() -> new IllegalArgumentException("투표를 찾을 수 없습니다."));

        AuthResponseDto authResponseDto = authFeignClient.findByNickname(closePollRequest.getNickname());
        if(!poll.getUserId().equals(authResponseDto.getId()))
            throw new IllegalArgumentException("투표를 생성한 회원만 종료할 수 있습니다..");

        if (poll.getVoteStatus() == VoteStatus.CLOSED) {
            throw new IllegalArgumentException("이미 종료된 투표입니다.");
        }

        poll.setVoteStatus(VoteStatus.CLOSED);
        pollRepository.save(poll);
        logger.info("투표 종료에 성공하였습니다. ");
    }


    //투표 종료시 이긴 팀 대중성 포인트 + 10
    public void awardPopularPoints(ClosePollRequest closePollRequest) {
        Poll poll = pollRepository.findById(closePollRequest.getPollId())
                .orElseThrow(() -> new IllegalArgumentException("투표를 찾을 수 없습니다."));

        List<Vote> votes = voteRepository.findByPoll(poll);
        logger.info("투표 아이디에 맞는 투표 결과를 찾았습니다.");

        // 각 선택지별 투표 수를 계산
        Map<Long, Long> choiceVotes = new HashMap<>();
        for (Vote vote : votes) {
            Long choiceId = vote.getChoice().getId();
            choiceVotes.put(choiceId, choiceVotes.getOrDefault(choiceId, 0L) + 1);
        }
        logger.info(choiceVotes.toString());

        // 가장 많이 투표받은 선택지 찾기
        Long mostVotedChoiceId = Collections.max(choiceVotes.entrySet(), Map.Entry.comparingByValue()).getKey();

        logger.info(String.valueOf(mostVotedChoiceId));
        // 해당 선택지를 투표한 유저들에게 popular_point 10씩 더하기
        List<Vote> votesForMostVotedChoice = votes.stream()
                .filter(vote -> vote.getChoice().getId().equals(mostVotedChoiceId))
                .collect(Collectors.toList());

        updatePopularPoints(votesForMostVotedChoice);
    }

    @Override // Feign
    public PollResponseDto findById(Long id) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("투표를 찾을 수 없습니다."));
        return new PollResponseDto(poll.getId());
    }

    private void updatePopularPoints(List<Vote> votes) {
        for (Vote vote : votes) {
            Long winUserId = vote.getUserId();
            logger.info(String.valueOf(winUserId));
            authFeignClient.plusPopularPoint(winUserId);
            //user.setPopular_point(user.getPopular_point() + 10);
        }
    }


    // Poll의 createdBy 필드 업데이트를 위한 새로운 메서드
//    @Transactional
//    public void updatePollCreatedBy(UserDto userDto) {
//        if(userDto.getNickname() != null) { // 추가: nickname null 체크
//            List<Poll> polls = pollRepository.findAllByCreatedBy(userDetails.getUsername()); // 수정: 모든 연관된 Poll 가져오기
//            if(polls != null) {
//                for (Poll poll : polls) {
//                    poll.setCreatedBy(userDto.getNickname());
//                    pollRepository.save(poll);
//                }
//            }
//            else{
//                log.info("생성한 poll이 없습니다. ");
//            }
//        }
//    }
}