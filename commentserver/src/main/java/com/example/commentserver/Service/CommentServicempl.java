package com.example.commentserver.Service;

import com.example.commentserver.Dto.AuthResponseDto;
import com.example.commentserver.Dto.CommentDto;
import com.example.commentserver.Dto.LikeDto;
import com.example.commentserver.Dto.PollResponseDto;
import com.example.commentserver.Entity.Comment;
import com.example.commentserver.Entity.CommentLike;
import com.example.commentserver.Entity.Report;
import com.example.commentserver.Enum.ReportReason;
import com.example.commentserver.Repository.CommentRepository;
import com.example.commentserver.Repository.LikeRepository;
import com.example.commentserver.Repository.ReportRepository;
import com.example.commentserver.Service.Feign.AuthFeignClient;
import com.example.commentserver.Service.Feign.PollFeignClient;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.commentserver.Dto.LikeDto.dtoToEntity;

@Slf4j
@Service
public class CommentServicempl implements CommentService{

    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final AuthFeignClient authFeignClient;
    private final PollFeignClient pollFeignClient;
    private final LikeRepository likeRepository;
    private final Storage storage;

    public CommentServicempl(@Autowired CommentRepository commentRepository,
                             ReportRepository reportRepository,
                             AuthFeignClient authFeignClient,
                             PollFeignClient pollFeignClient,
                             LikeRepository likeRepository,
                             Storage storage) {
        this.commentRepository = commentRepository;
        this.reportRepository = reportRepository;
        this.storage = storage;
        this.authFeignClient = authFeignClient;
        this.likeRepository = likeRepository;
        this.pollFeignClient = pollFeignClient;
    }

    //댓글 생성
    @Override
    public CommentDto save(CommentDto commentDto, String uid, Long pollId, MultipartFile mediaFile) {

        AuthResponseDto authResponseDto = authFeignClient.findByUid(uid);
        if (authResponseDto == null) {
            throw new IllegalArgumentException(uid +"가 존재하지 않습니다.");
        }

        PollResponseDto pollResponseDto = pollFeignClient.findById(pollId);
        if(pollResponseDto == null){
                throw new IllegalArgumentException("투표 번호가 " + pollId + "번인 투표가 존재하지 않습니다.");
        }

        String mediaUrl = null;
        if (mediaFile != null && !mediaFile.isEmpty()) {
            try {
                UUID uuid = UUID.randomUUID();
                String fileExtension = Objects.requireNonNull(mediaFile.getOriginalFilename()).substring(mediaFile.getOriginalFilename().lastIndexOf("."));
                String fileName = uuid + fileExtension;
                String contentType = switch (fileExtension.toLowerCase()) {
                    case ".jpg", ".jpeg" -> "image/jpeg";
                    case ".png" -> "image/png";
                    case ".bmp" -> "image/bmp";
                    case ".gif" -> "image/gif";
                    case ".mp4" -> "video/mp4";
                    case ".avi" -> "video/avi";
                    case ".wmv" -> "video/wmv";
                    case ".mpeg:" -> "video/mpeg";
                    default -> "application/octet-stream";
                };
                BlobId blobId = BlobId.of("comment_media", fileName);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                        .setContentType(contentType)
                        .setContentDisposition("inline; filename=" + mediaFile.getOriginalFilename())
                        .build();
                storage.create(blobInfo, mediaFile.getBytes());
                mediaUrl = "https://storage.googleapis.com/comment_media/" + fileName;
            } catch (IOException e) {
                throw new RuntimeException("미디어 파일 업로드 중 오류가 발생했습니다.", e);
            }
        }

        Comment comment = commentDto.dtotoEntity();
        comment.setMediaUrl(mediaUrl);
        comment.setUserId(authResponseDto.getId());
        comment.setPollId(pollResponseDto.getId());
        Comment savedComment = commentRepository.save(comment);
        log.info("댓글이 성공적으로 작성되었습니다!");

        return CommentDto.entityToDto(savedComment,authResponseDto);
    }

    //대댓글 생성
    @Override
    public CommentDto addComment(Long commentId, CommentDto commentDto, String uid, Long pollId, MultipartFile mediaFile) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 아이디가 " + commentId + "번인 댓글이 존재하지 않습니다."));


        AuthResponseDto authResponseDto = authFeignClient.findByUid(uid);
        if (authResponseDto == null) {
            throw new IllegalArgumentException(uid + "가 존재하지 않습니다.");
        }

        PollResponseDto pollResponseDto = pollFeignClient.findById(pollId);
        if(pollResponseDto == null){
            throw new IllegalArgumentException("투표 번호가 " + pollId + "번인 투표가 존재하지 않습니다.");
        }

        String mediaUrl = null;
        if (mediaFile != null && !mediaFile.isEmpty()) {
            try {
                UUID uuid = UUID.randomUUID();
                String fileExtension = Objects.requireNonNull(mediaFile.getOriginalFilename()).substring(mediaFile.getOriginalFilename().lastIndexOf("."));
                String fileName = uuid + fileExtension;
                String contentType = switch (fileExtension.toLowerCase()) {
                    case ".jpg", ".jpeg" -> "image/jpeg";
                    case ".png" -> "image/png";
                    case ".bmp" -> "image/bmp";
                    case ".gif" -> "image/gif";
                    case ".mp4" -> "video/mp4";
                    case ".avi" -> "video/avi";
                    case ".wmv" -> "video/wmv";
                    case ".mpeg:" -> "video/mpeg";
                    default -> "application/octet-stream";
                };
                BlobId blobId = BlobId.of("comment_media", fileName);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                        .setContentType(contentType)
                        .setContentDisposition("inline; filename=" + mediaFile.getOriginalFilename())
                        .build();
                storage.create(blobInfo, mediaFile.getBytes());
                mediaUrl = "https://storage.googleapis.com/comment_media/" + fileName;
            } catch (IOException e) {
                throw new RuntimeException("미디어 파일 업로드 중 오류가 발생했습니다.", e);
            }
        }
        Comment comment = commentDto.dtotoEntity();
        comment.setParentComment(parentComment);
        comment.setUserId(authResponseDto.getId());
        comment.setPollId(pollResponseDto.getId());
        parentComment.setUserId(authResponseDto.getId());
        parentComment.setPollId(pollResponseDto.getId());
        comment.setMediaUrl(mediaUrl);

        if (!parentComment.getPollId().equals(pollResponseDto.getId())) {
            throw new IllegalArgumentException("댓글을 작성한 같은 투표에만 대댓글을 작성 할 수 있습니다.");
        }

        Comment savedComment = commentRepository.save(comment);
        log.info("해당 댓글의 답글이 성공적으로 추가되었습니다!");

        return CommentDto.entityToDto(savedComment,authResponseDto);
    }


    //댓글 신고
    @Override
    public CommentDto reportComment(String uid, Long pollId, Long commentId, String reportReasonString) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 번호가 " + commentId + "번인 댓글이 존재하지 않습니다."));

        AuthResponseDto authResponseDto = authFeignClient.findByUid(uid);
        if (authResponseDto == null) {
            throw new IllegalArgumentException(uid + "가 존재하지 않습니다.");
        }

        PollResponseDto pollResponseDto = pollFeignClient.findById(pollId);
        if(pollResponseDto == null){
            throw new IllegalArgumentException("투표 번호가 " + pollId + "번인 투표가 존재하지 않습니다.");
        }

        if (!comment.getPollId().equals(pollId)) {
            throw new IllegalArgumentException("해당 투표의 댓글이 아닙니다. 투표 아이디나 댓글 아이디를 다시 설정해 주세요.");
        }

        ReportReason reportReason = ReportReason.valueOf(reportReasonString);
        Optional<Report> report = reportRepository.findByUserIdAndCommentId(authResponseDto.getId(), comment.getId());

        if (report.isPresent()) {
            report.get().setReason(reportReason);
            comment.setReportCount(comment.getReportCount() + 1);
            reportRepository.save(report.get());
        } else
            comment.setReportCount(comment.getReportCount() + 1);
        if (comment.getReportCount() >= 3) {
                comment.setContent("해당 댓글은 신고되어 삭제되었습니다");
                comment.setMediaUrl(null);
            }
            comment.setReportReason(reportReason);
            commentRepository.save(comment);

            Report newReport = new Report();
            newReport.setUserId(authResponseDto.getId());
            newReport.setComment(comment);
            newReport.setReason(reportReason);
            reportRepository.save(newReport);

        log.info("댓글이 성공적으로 신고되었습니다!");
        return CommentDto.entityToDto(comment,authResponseDto);
    }

    @Override
    public void likeComment(LikeDto likeDto) throws AccessDeniedException {
        Comment comment = commentRepository.findById(likeDto.getCommentId())
                .orElseThrow(() -> new RuntimeException("투표를 찾을 수 없습니다."));

        if(likeRepository.existsByUidAndCommentId(likeDto.getUid(),likeDto.getCommentId()))
            throw new AccessDeniedException("이미 좋아요를 눌렀습니다.");

        CommentLike commentLike = dtoToEntity(likeDto);
        likeRepository.save(commentLike);
        comment.setLikes(comment.getLikes() + 1);
        commentRepository.save(comment);
    }

    //해당 투표 전체 댓글 조회
    @Override
    public List<CommentDto> getCommentsByPollId(Long pollId) {
        PollResponseDto pollResponseDto = pollFeignClient.findById(pollId);
        if(pollResponseDto == null){
            throw new IllegalArgumentException("투표 번호가 " + pollId + "번인 투표가 존재하지 않습니다.");
        }
        List<Comment> comments = commentRepository.findByPollId(pollResponseDto.getId());
        return comments.stream()
                .map(comment -> CommentDto.entityToDto(comment, authFeignClient.findById(comment.getUserId())))
                .collect(Collectors.toList());
    }

    //댓글 조회
    @Override
    public CommentDto findById(Long commentId) {
        try {
            Optional<Comment> commentData = commentRepository.findById(commentId);
            if (commentData.isPresent()) {
                Comment comment = commentData.get();
                return CommentDto.entityToDto(comment, authFeignClient.findById(comment.getUserId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //댓글 수정
    @Override
    public CommentDto update(Long commentId, CommentDto commentDto) {
        try {
            AuthResponseDto authResponseDto = authFeignClient.findById(commentDto.getUserId());
            Optional<Comment> commentData = commentRepository.findById(commentId);
            if (commentData.isPresent()) {
                Comment comment = commentData.get();
                comment.setContent(commentDto.getContent());

                Comment updatedComment = commentRepository.save(comment);
                return CommentDto.entityToDto(updatedComment,authResponseDto);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //댓글 삭제
    @Override
    public void delete(Long commentId) {
        try {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new IllegalArgumentException("댓글 아이디가 " + commentId + "번인 댓글이 존재하지 않습니다."));
            // 댓글과 관련된 신고 데이터 삭제
            likeRepository.deleteById(commentId);
            List<Report> reportList = reportRepository.findByCommentId(commentId);
            reportRepository.deleteAll(reportList);

            // 대댓글 삭제
            deleteChildrenComments(comment);

            // 댓글 삭제
            commentRepository.delete(comment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Long countByUserId(Long userId) {
        return commentRepository.countByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteByPoll(Long pollId) {
        List<Comment> comments = commentRepository.findByPollId(pollId);
        for (Comment comment : comments) {
            reportRepository.deleteByCommentId(comment.getId());
            likeRepository.deleteByCommentId(comment.getId());
        }
        commentRepository.deleteByPollId(pollId);
    }

    //대댓글 삭제 메서드
    private void deleteChildrenComments(Comment comment) {
        Queue<Comment> queue = new LinkedList<>();
        queue.offer(comment);
        //재귀 호출로 인한 성능 이슈를 해결하기 위해 반복문을 사용하여 대댓글을 삭제하는 알고리즘
        while (!queue.isEmpty()) {
            Comment current = queue.poll();
            List<Comment> childrenComments = current.getChildrenComment();

            for (Comment childComment : childrenComments) {
                // 대댓글과 관련된 신고 데이터 삭제
                List<Report> reportList = reportRepository.findByCommentId(childComment.getId());
                reportRepository.deleteAll(reportList);

                // 대댓글의 대댓글을 큐에 추가
                queue.offer(childComment);
            }

            // 현재 댓글 삭제
            commentRepository.delete(current);
        }
    }
}
