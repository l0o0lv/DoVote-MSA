package com.example.pollserver.Repository;

import com.example.pollserver.Entity.PollLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<PollLike,Long> {
    boolean existsByUserIdAndPollId(Long userId, Long pollId);
}
