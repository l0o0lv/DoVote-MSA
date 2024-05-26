package com.example.commentserver.Entity;

import com.example.commentserver.Enum.ReportReason;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name="comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long pollId;
    private String content;
    private String mediaUrl;
    private int likes = 0;
    private int reportCount = 0;

    @CreationTimestamp
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", orphanRemoval = true)
    @JsonIgnore
    private List<Comment> childrenComment = new ArrayList<>();

    @Column(name = "report_reason")
    @Enumerated(EnumType.STRING)
    private ReportReason reportReason;
}