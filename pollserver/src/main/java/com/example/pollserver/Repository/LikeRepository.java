package com.example.pollserver.Repository;

import com.example.pollserver.Entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like,Long> {
    boolean existsByUserIdAndPollId(Long userId, Long pollId);
}
