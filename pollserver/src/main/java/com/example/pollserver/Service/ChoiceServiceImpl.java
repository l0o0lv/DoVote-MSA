package com.example.pollserver.Service;

import com.example.pollserver.Dto.Choice.ChoiceDto;
import com.example.pollserver.Dto.Choice.ChoiceRequest;
import com.example.pollserver.Entity.Choice;
import com.example.pollserver.Entity.Poll;
import com.example.pollserver.Repository.ChoiceRepository;
import com.example.pollserver.Repository.PollRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ChoiceServiceImpl implements ChoiceService{


    private static final Logger logger = LoggerFactory.getLogger(ChoiceServiceImpl.class);
    private final PollRepository pollRepository;
    private final ChoiceRepository choiceRepository;

    @Autowired
    public ChoiceServiceImpl(PollRepository pollRepository, ChoiceRepository choiceRepository) {
        this.pollRepository = pollRepository;
        this.choiceRepository = choiceRepository;
    }

    public void saveChoices(Long pollId, ChoiceRequest choiceRequest) {
        // 투표(Poll) ID로 해당 투표를 찾습니다.
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("투표를 찾을 수 없습니다. ID: " + pollId));

        // 선택지 저장
        List<ChoiceDto> choiceDtos = choiceRequest.getChoiceDtos();
        List<Choice> choices = choiceDtos.stream()
                .map(choiceDto -> {
                    Choice choice = choiceDto.dtoToEntity();
                    choice.setPoll(poll);
                    return choiceRepository.save(choice); // 선택지를 저장하고 엔티티를 반환
                })
                .toList();

        // 투표와 선택지 연관시키고 저장
        poll.getChoices().clear();  // 컬렉션 비우기
        for (Choice choice : choices) {
            choice.setPoll(poll);
        }
        poll.getChoices().addAll(choices);

        pollRepository.save(poll);
        logger.info("선택지 저장 및 투표와 연관시키기 완료 (Service)");
    }


}
