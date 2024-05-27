package com.example.pollserver.Repository;

import com.example.pollserver.Entity.Poll;
import com.example.pollserver.Enum.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findByTitleContaining(String title);
    List<Poll> findByCategory(Category category);
    // 현재 유저가 생성한 투표의 수를 세는 메서드
    long countByUserId(Long userId);
    List<Poll> findAllByCreatedBy(String createdBy);
}



