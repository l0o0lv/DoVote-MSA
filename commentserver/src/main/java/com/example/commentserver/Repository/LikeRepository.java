package com.example.commentserver.Repository;

import com.example.commentserver.Entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<CommentLike,Long> {
    boolean existsByUidAndCommentId(String uid, Long commentId);
}
