package com.example.commentserver.Dto;

import com.example.commentserver.Entity.Comment;
import com.example.commentserver.Enum.ReportReason;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;
    private Long userId;
    private Long pollId;
    private String userNickname;
    private String content;
    private CommentDto parentComment;
    private List<CommentDto> childrenComment;
    private int likes;
    private String mediaUrl;
    private LocalDateTime time;
    private int reportCount = 0;
    private ReportReason reportReason;

    public static CommentDto entityToDto(Comment comment,AuthResponseDto authResponseDto) {
        CommentDto parentCommentDto = null;
        if (comment.getParentComment() != null) {
            parentCommentDto = new CommentDto(
                    comment.getParentComment().getId(),
                    comment.getParentComment().getUserId(),
                    comment.getParentComment().getPollId(),
                    authResponseDto.getNickname(),
                    comment.getParentComment().getContent(),
                    null,
                    null,
                    comment.getParentComment().getLikes(),
                    comment.getParentComment().getMediaUrl(),
                    comment.getParentComment().getTime(),
                    comment.getParentComment().getReportCount(),
                    comment.getParentComment().getReportReason()
            );
        }

        List<CommentDto> childrenCommentDtos = comment.getChildrenComment().stream()
                .map(childComment -> new CommentDto(
                        childComment.getId(),
                        childComment.getUserId(),
                        childComment.getPollId(),
                        authResponseDto.getNickname(),
                        childComment.getContent(),
                        null,
                        null,
                        childComment.getLikes(),
                        childComment.getMediaUrl(),
                        childComment.getTime(),
                        childComment.getReportCount(),
                        childComment.getReportReason()
                ))
                .collect(Collectors.toList());

        return new CommentDto(
                comment.getId(),
                comment.getUserId(),
                comment.getPollId(),
                authResponseDto.getNickname(),
                comment.getContent(),
                parentCommentDto,
                childrenCommentDtos,
                comment.getLikes(),
                comment.getMediaUrl(),
                comment.getTime(),
                comment.getReportCount(),
                comment.getReportReason()
        );
    }

    public Comment dtotoEntity() {
        Comment comment = new Comment();
        comment.setContent(this.content);
        comment.setUserId(this.userId);
        comment.setPollId(this.pollId);
        comment.setLikes(this.likes);
        comment.setMediaUrl(this.mediaUrl);
        comment.setTime(this.time);
        comment.setReportCount(this.reportCount);
        comment.setReportReason(this.reportReason);
        return comment;
    }
}