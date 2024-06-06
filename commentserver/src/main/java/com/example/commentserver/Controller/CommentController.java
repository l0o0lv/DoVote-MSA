package com.example.commentserver.Controller;

import com.example.commentserver.Dto.CommentDto;
import com.example.commentserver.Service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comments")

public class CommentController {

    // 2023.12.03 17:54 의존성 주입 경고 제거를 위해 이렇게 변경 했습니다.
    private final CommentService commentService;

    public CommentController(@Autowired CommentService commentService){
        this.commentService = commentService;
    }

    //댓글 생성
    @SneakyThrows
    @PostMapping(value = "/{uid}/{pollId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommentDto> save(@PathVariable String uid, @PathVariable Long pollId, @RequestPart("content") String content, @RequestPart(value = "mediaData", required = false) MultipartFile mediaData) {
        ObjectMapper mapper = new ObjectMapper();
        CommentDto commentDto = mapper.readValue(content, CommentDto.class);
        CommentDto savedComment = commentService.save(commentDto, uid, pollId, mediaData);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }

    //대댓글 생성
    @SneakyThrows
    @PostMapping(value = "/{uid}/{pollId}/{commentId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommentDto> addComment(@PathVariable String uid, @PathVariable Long pollId, @PathVariable Long commentId, @RequestPart("content") String content, @RequestPart(value = "mediaData", required = false) MultipartFile mediaData) {
        ObjectMapper mapper = new ObjectMapper();
        CommentDto commentDto = mapper.readValue(content, CommentDto.class);
        CommentDto savedComment = commentService.addComment(commentId, commentDto, uid, pollId, mediaData);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }

//    //댓글 좋아요
//    @PostMapping("/like/{uid}/{pollId}/{commentId}")
//    public ResponseEntity<CommentDto> likeOrUnlikeComment(@PathVariable String uid, @PathVariable Long pollId, @PathVariable Long commentId) {
//        CommentDto updatedComment = commentService.likeOrUnlikeComment(uid, pollId, commentId);
//        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
//    }

    //댓글 신고
    @SneakyThrows
    @PostMapping("/report/{uid}/{pollId}/{commentId}")
    public ResponseEntity<CommentDto> reportComment(@PathVariable String uid, @PathVariable Long pollId, @PathVariable Long commentId, @RequestBody String reportReason) {
        ObjectMapper mapper = new ObjectMapper();
        CommentDto commentDto = mapper.readValue(reportReason, CommentDto.class);
        CommentDto updatedComment = commentService.reportComment(uid, pollId, commentId, commentDto.getReportReason().toString());
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

//    //해당 댓글 쪽지 전송
//    @SneakyThrows
//    @PostMapping("/message/{uid}/{pollId}/{commentId}")
//    public ResponseEntity<Void> sendMessageFromComment(@PathVariable String uid, @PathVariable Long pollId, @PathVariable Long commentId, @RequestBody String content) {
//        ObjectMapper mapper = new ObjectMapper();
//        MessageDto messageDto = mapper.readValue(content, MessageDto.class);
//        commentService.sendMessageFromComment(uid, pollId, commentId, messageDto);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    //댓글 쪽지 조회
//    @GetMapping("/messages/{commentId}")
//    public ResponseEntity<List<MessageDto>> getMessagesFromComment(@PathVariable Long commentId) {
//        List<MessageDto> messages = commentService.getMessagesFromComment(commentId);
//        return new ResponseEntity<>(messages, HttpStatus.OK);
//    }

    //해당 투표 전체 댓글 조회
    @GetMapping("/poll/{pollId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPollId(@PathVariable Long pollId) {
        List<CommentDto> comments = commentService.getCommentsByPollId(pollId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    //댓글 조회
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> findById(@PathVariable Long commentId) {
        CommentDto comment = commentService.findById(commentId);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    //댓글 수정
    @SneakyThrows
    @PutMapping(value = "/{commentId}")
    public ResponseEntity<CommentDto> update(@PathVariable Long commentId, @RequestBody String content) {
        ObjectMapper mapper = new ObjectMapper();
        CommentDto commentDto = mapper.readValue(content, CommentDto.class);
        CommentDto updatedComment = commentService.update(commentId, commentDto);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping("/cascade/{pollId}")
    public void deleteComments(@PathVariable Long pollId){
        commentService.deleteByPoll(pollId);
    }

    @GetMapping("/count/{userId}")
    public Long countByUserId(@PathVariable Long userId) {
        return commentService.countByUserId(userId);
    }
}
