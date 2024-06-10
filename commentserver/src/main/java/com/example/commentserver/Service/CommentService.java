package com.example.commentserver.Service;

import com.example.commentserver.Dto.CommentDto;
import com.example.commentserver.Dto.LikeDto;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface CommentService {
    CommentDto save(CommentDto commentDto, String uid, Long pollId, MultipartFile mediaFile);
    CommentDto addComment(Long commentId, CommentDto commentDto, String uid, Long pollId, MultipartFile mediaData);
//    CommentDto likeOrUnlikeComment(String uid, Long pollId, Long commentId);
    CommentDto reportComment(String uid, Long pollId, Long commentId, String reportReasonString);

    void likeComment(LikeDto likeDto) throws AccessDeniedException;
    List<CommentDto> getCommentsByPollId(Long pollId);
    CommentDto findById(Long commentId);
    CommentDto update(Long commentId, CommentDto commentDto);
    void delete(Long commentId);

    Long countByUserId(Long userId);

    void deleteByPoll(Long pollId);
}