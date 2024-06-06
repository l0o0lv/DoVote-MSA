package com.example.commentserver.Repository;


import com.example.commentserver.Entity.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPollId(Long pollId);
    @Transactional
    void deleteByPollId(Long pollId);

    Long countByUserId(Long userId);
}
