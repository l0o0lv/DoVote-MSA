package com.example.pollserver.Repository;

import com.example.pollserver.Entity.Poll;
import com.example.pollserver.Entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    // 주어진 Poll과 UserEntity에 해당하는 Vote 엔티티가 존재하는지 여부를 확인
    boolean existsByPollIdAndUserId(Long pollId, Long userId);

    List<Vote> findByNickname(String nickname);

    Long countByUserId(Long userId);

    List<Vote> findByPoll(Poll poll);
    List<Vote> findByUserId(Long userId);

    //선택지의 선택 된 수 반환
    @Query("SELECT v.choice.id, v.choice.text, COUNT(v) FROM Vote v WHERE v.poll.id = :pollId GROUP BY v.choice.id, v.choice.text")
    List<Object[]> countSelectedOptionsByPollId(Long pollId);


    //현재 유저와 같은 선택지를 고른 유저의 닉네임 반환
    @Query("SELECT v.nickname FROM Vote v " +
            "WHERE v.poll.id = :pollId " +
            "AND v.choice.id = :choiceId " +
            "AND v.nickname <> :excludeNickname") // 선택지를 고른 다른 사용자들의 닉네임을 가져오기 위해 현재 사용자는 제외
    List<String> findUserNicknamesByVoteAndChoice(
            @Param("pollId") Long pollId,
            @Param("choiceId") Long choiceId,
            @Param("excludeNickname") String excludeNickname
    );

    List<Vote> findAllByNickname(String nickname);

    void deleteByPollId(Long id);
}

