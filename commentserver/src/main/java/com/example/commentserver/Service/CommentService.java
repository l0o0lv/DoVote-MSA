package com.example.commentserver.Service;

import com.example.commentserver.Dto.CommentDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommentService {
    CommentDto save(CommentDto commentDto, String uid, Long pollId, MultipartFile mediaFile);
    CommentDto addComment(Long commentId, CommentDto commentDto, String uid, Long pollId, MultipartFile mediaData);
//    CommentDto likeOrUnlikeComment(String uid, Long pollId, Long commentId);
    CommentDto reportComment(String uid, Long pollId, Long commentId, String reportReasonString);

//    void sendMessageFromComment(String uid, Long pollId, Long commentId, MessageDto messageDto);
//    List<MessageDto> getMessagesFromComment(Long commentId);
    List<CommentDto> getCommentsByPollId(Long pollId);
    CommentDto findById(Long commentId);
    CommentDto update(Long commentId, CommentDto commentDto);
    void delete(Long commentId);
}