package com.example.pollserver.Controller;

import com.example.pollserver.Dto.Feign.PollResponseDto;
import com.example.pollserver.Dto.Poll.*;
import com.example.pollserver.Enum.Category;
import com.example.pollserver.Service.PollService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.google.cloud.storage.Storage;
import org.springframework.http.MediaType;

@Slf4j
@RestController
@RequestMapping("/polls")
public class PollController {

    private static final Logger logger = LoggerFactory.getLogger(PollController.class);

    private final PollService pollService;

    private final Storage storage;

    public PollController(@Autowired PollService pollService, Storage storage) {
        this.pollService = pollService;
        this.storage = storage;
    }

    //투표 생성 (수정) //TODO : JWT 필터 필요
    @PostMapping(value = "/upload", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PollDto> createPollWithChoices(
            @RequestPart("pollData") String pollData,
            @RequestPart(value = "mediaData", required = false) MultipartFile mediaData) {
        log.info("투표 생성 시작");
        try {
            ObjectMapper mapper = new ObjectMapper();
            PollRequest pollRequest = mapper.readValue(pollData, PollRequest.class);
            pollRequest.setCreatedBy(pollRequest.getCreatedBy());
            if (mediaData != null) {
                String[] mediaFileData = saveMediaFile(mediaData).split(","); //미디어 파일 저장
                pollRequest.setMediaName(mediaFileData[0]); //미디어 이름 설정
                pollRequest.setMediaUrl(mediaFileData[1]); //미디어 url 설정
            }
            log.info("createPollWithChices 실행 전");
            PollDto createdPollDto = pollService.createPollWithChoices(pollRequest, pollRequest.getTitle(), pollRequest.getQuestion(), pollRequest.getCategory());
            log.info("선택지 제외 한 투표 생성 완료. ");
            return new ResponseEntity<>(createdPollDto, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //전체 투표 내용 조회
    @GetMapping("/all")
    public List<PollResponse> getAllPolls() {
        return pollService.getAllPolls();
    }

    //카테고리 별 투표 내용 조회
    @GetMapping("/category")
    public ResponseEntity<List<PollResponse>> getPollsByCategory(@RequestParam("category") String category) {
        try {
            Category enumCategory = Category.valueOf(category);
            List<PollResponse> pollResponses = pollService.getPollsByCategory(enumCategory);
            return ResponseEntity.ok(pollResponses);
        } catch (IllegalArgumentException e) {
            // 클라이언트가 올바르지 않은 카테고리를 제공한 경우
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            // 서비스에서 처리하지 못한 다른 예외
            return ResponseEntity.status(500).body(null);
        }
    }

    //제목 투표 조회
    @GetMapping("/search")
    public ResponseEntity<List<PollResponse>> getPollsByTitle(@RequestParam("title") String title) {
        List<PollResponse> pollResponses = pollService.getPollsByTitle(title);
        return ResponseEntity.ok(pollResponses);
    }

    // 좋아요 기능
    @PostMapping("/likes") //TODO : JWT 필터 필요
    public ResponseEntity<?> likePoll(@RequestBody LikeDto likeDto) {
        pollService.likePoll(likeDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoll(@PathVariable Long id) {
        pollService.deletePoll(id);
        return ResponseEntity.noContent().build();
    }

    //투표 생성 수 반환
    @GetMapping("/created-count/{nickname}")
    public  ResponseEntity<Long> getCreatedPollCount(@PathVariable String nickname) {
        long createdPollCount = pollService.getCreatedPollCount(nickname);
        // 로그 추가
        logger.info("투표 생성 수 리턴에 성공했습니다. 사용자: {}, 생성된 투표 수: {}", nickname, createdPollCount);
        return ResponseEntity.ok(createdPollCount);
    }

    //투표 참여 수 반환
    @GetMapping("/participated-count/{nickname}")
    public ResponseEntity<Long> getParticipatedPollCount(@PathVariable String nickname) {

        long participatePollCount = pollService.getParticipatedPollCount(nickname);

        logger.info("투표 참여 수 리턴에 성공했습니다. 사용자: {}, 참여한 투표 수: {}", nickname, participatePollCount);

        return ResponseEntity.ok(participatePollCount);
    }

    //투표 종료 기능
    @PostMapping("/close") //TODO : JWT 필터 필요
    public ResponseEntity<String> closePoll(@RequestBody ClosePollRequest closePollRequest) {
        try {
            pollService.closePoll(closePollRequest);
            logger.info("투표 종료 성공");
            return ResponseEntity.ok("투표가 성공적으로 종료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //대중성 포인트 올리기
    @PostMapping("/popularpoint")
    public ResponseEntity<String> pluspopularpoint(@RequestBody ClosePollRequest closePollRequest) {
        try {
            logger.info("대중성 포인트 올리기를 시작 합니다. ");
            pollService.awardPopularPoints(closePollRequest);
            logger.info("대중성 포인트 + 10 성공");
            return ResponseEntity.ok("대중성 포인트가 성공적으로 올라갔습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    //현재 유저의 대중성 포인트 리턴
    @GetMapping("/popular-point/{nickname}")
    public ResponseEntity<Long> getPopularPoint(@PathVariable String nickname) {
        long popularpoint = pollService.getPopularPointByUsername(nickname);
        return ResponseEntity.ok(popularpoint);
    }
    //미디어 파일 경로 설정
    @SneakyThrows
    private String saveMediaFile(MultipartFile mediaFile) {
        //원본 파일명
        String originalFileName = mediaFile.getOriginalFilename();

        //파일명에 특수문자가 있으면 문제가 생길 수 있으므로 영문과 숫자로만 이루어진 파일명으로 변경
        String safeFileName = UUID.randomUUID().toString();

        //파일 확장자
        String extension = Objects.requireNonNull(originalFileName).substring(originalFileName.lastIndexOf("."));

        //파일명
        String fileName = safeFileName + extension;

        //contentType 설정
        String contentType = switch (extension) {
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

        //구글 클라우드 스토리지에 파일 업로드
        BlobId blobId = BlobId.of("comment_media", fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
        storage.create(blobInfo, mediaFile.getInputStream());

        //파일 URL
        String fileUrl = "https://storage.googleapis.com/comment_media/" + fileName;

        //파일 이름과 URL을 리턴
        return fileName + "," + fileUrl;
    }
    @GetMapping("/find/{id}")
    public PollResponseDto findById(@PathVariable Long id) {
        return pollService.findById(id);
    }
}