package com.example.commentserver.Repository;

import com.example.commentserver.Entity.Report;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUserIdAndCommentId(Long userId, Long commentId);
    List<Report> findByCommentId(Long commentId);

    @Transactional
    void deleteByCommentId(Long id);
}
