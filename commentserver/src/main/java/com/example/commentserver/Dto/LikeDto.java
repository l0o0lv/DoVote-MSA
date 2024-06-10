package com.example.commentserver.Dto;

import com.example.commentserver.Entity.Comment;
import com.example.commentserver.Entity.CommentLike;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeDto {
    private String uid;
    private Long commentId;

    public static CommentLike dtoToEntity(LikeDto likeDto){
        Comment comment = Comment.builder().id(likeDto.getCommentId()).build();

        return CommentLike.builder()
                .uid(likeDto.getUid())
                .comment(comment)
                .build();
    }
}
