package com.example.pollserver.Service;

import com.example.pollserver.Dto.Feign.AuthResponseDto;
import com.example.pollserver.Dto.Vote.VoteDto;
import com.example.pollserver.Dto.Vote.VoteResultDto;
import com.example.pollserver.Entity.Choice;
import com.example.pollserver.Entity.Poll;
import com.example.pollserver.Entity.Vote;
import com.example.pollserver.Repository.ChoiceRepository;
import com.example.pollserver.Repository.PollRepository;
import com.example.pollserver.Repository.VoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VoteServiceImpl implements VoteService {


    private static final Logger logger = LoggerFactory.getLogger(VoteServiceImpl.class);
    private final VoteRepository voteRepository;
    private final PollRepository pollRepository;
    private final ChoiceRepository choiceRepository;
    private final AuthFeignClient authFeignClient;
    @Autowired
    public VoteServiceImpl(
            VoteRepository voteRepository,
            PollRepository pollRepository,
            ChoiceRepository choiceRepository,
            AuthFeignClient authFeignClient){
        this.voteRepository = voteRepository;
        this.pollRepository = pollRepository;
        this.choiceRepository = choiceRepository;
        this.authFeignClient = authFeignClient;
    }
    @Transactional
    public void vote(VoteDto voteDto) {
        logger.info("VoteServiceImpl(vote) 실행");

        //userNickname에 해당하는 사용자 정보를 데이터베이스에서 찾아 UserEntity 객체로 가져옴
        AuthResponseDto authResponseDto = authFeignClient.findByNickname(voteDto.getNickname());

        // pollId에 해당하는 투표 정보를 데이터베이스에서 찾아 Poll 객체로 가져옴
        Poll poll = pollRepository.findById(voteDto.getPollId())
                .orElseThrow(() -> new RuntimeException("해당 투표를 찾을 수 없습니다."));

        //choiceId에 해당하는 선택지 정보를 데이터베이스에서 찾아 Choice 객체로 가져옴
        Choice choice = choiceRepository.findById(voteDto.getChoiceId())
                .orElseThrow(() -> new RuntimeException("해당 선택지를 찾을 수 없습니다."));

        if (voteRepository.existsByPollIdAndUserId(poll.getId(), authResponseDto.getId())) {
            throw new RuntimeException("이미 투표한 투표입니다.");
        }

        // Vote 객체에 투표, 선택지, 사용자 정보를 설정
        Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setChoice(choice);
        vote.setUserId(authResponseDto.getId());
        vote.setNickname(voteDto.getNickname());
        voteRepository.save(vote);

        logger.info("투표 성공!");
    }

    //유저 닉네임으로 투표 결과 프론트한테 반환 해주기
    public List<VoteDto> getVotesByNickname(String nickname) {

        AuthResponseDto authResponseDto = authFeignClient.findByNickname(nickname);

        List<Vote> votes = voteRepository.findByNickname(nickname);
        return votes.stream()
                .map(vote -> new VoteDto(vote, authResponseDto))
                .collect(Collectors.toList());
    }
    public List<Map<String, Object>> getSelectedOptionCounts(Long pollId) {
        List<Object[]> results = voteRepository.countSelectedOptionsByPollId(pollId);

        List<Map<String, Object>> selectedOptionCounts = new ArrayList<>();

        for (Object[] result : results) {
            Long choiceId = (Long) result[0];
            String choiceText = (String) result[1];
            Long count = ((Number) result[2]).longValue();

            Map<String, Object> choiceData = new HashMap<>();
            choiceData.put("choice_id", choiceId);
            choiceData.put("text", choiceText);
            choiceData.put("count", count);

            selectedOptionCounts.add(choiceData);
        }

        return selectedOptionCounts;
    }


    //현재 유저와 같은 선택지를 고른 유저의 닉네임 반환
    public List<String> findUserNicknamesByVoteAndChoice(Long pollId, Long choiceId, String nickname) {
        return voteRepository.findUserNicknamesByVoteAndChoice(pollId, choiceId, nickname);
    }

    //연령별, 성별별 투표 결과 리턴
    public VoteResultDto getVoteResultByPollId(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("투표를 찾을 수 없습니다."));

        List<Vote> votes = voteRepository.findByPoll(poll);
        VoteResultDto voteResult = new VoteResultDto();

        for (Choice choice : poll.getChoices()) {
            Map<String, Integer> genderCount = new HashMap<>();
            Map<String, Integer> ageCount = new HashMap<>();

            for (Vote vote : votes) {
                if (vote.getChoice().equals(choice)) {
                    Long userId = vote.getId();
                    AuthResponseDto user = authFeignClient.findById(userId);

                    if (user != null) {
                        String gender = user.getGender();
                        String age = user.getAge();

                        genderCount.put(gender, genderCount.getOrDefault(gender, 0) + 1);
                        String ageGroup = getAgeGroup(age);
                        ageCount.put(ageGroup, ageCount.getOrDefault(ageGroup, 0) + 1);
                    }
                }
            }

            voteResult.getChoiceName().put(choice.getId(), choice.getText());
            voteResult.getChoiceGenderCounts().put(choice.getId(), genderCount);
            voteResult.getChoiceAgeCounts().put(choice.getId(), ageCount);

        }

        return voteResult;
    }

    // 연령대를 구하는 메서드
    private String getAgeGroup(String age) {
        int ageInt = Integer.parseInt(age);
        if (ageInt >= 10 && ageInt < 20) {
            return "10대";
        } else if (ageInt >= 20 && ageInt < 30) {
            return "20대";
        } else if (ageInt >= 30 && ageInt < 40) {
            return "30대";
        } else if (ageInt >= 40 && ageInt < 50) {
            return "40대";
        } else if (ageInt >= 50 && ageInt < 60) {
            return "50대";
        } else if (ageInt >= 60 && ageInt < 70) {
            return "60대";
        }else{
            return "70대 이상";
        }
    }
}


