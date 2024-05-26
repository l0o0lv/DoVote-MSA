package com.example.commentserver.Entity;

import com.example.commentserver.Enum.ReportReason;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "report")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    private Comment comment; //댓글 신고

    @Enumerated(EnumType.STRING)
    private ReportReason reason;
}
