package com.example.authserver.Controller;

import com.example.authserver.Dto.AuthDto;
import com.example.authserver.Dto.AuthResponseDto;
import com.example.authserver.Dto.TokenDto;
import com.example.authserver.Enum.Category;
import com.example.authserver.Service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final static Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup") // 회원가입
    public ResponseEntity<AuthDto> signUp(@RequestBody @Valid AuthDto authDto) {
        AuthDto createUser = authService.Signup(authDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createUser);
    }

    @PostMapping("/signin") // 로그인
    public ResponseEntity<TokenDto> signIn(@RequestBody AuthDto authDto) {
        TokenDto login = authService.SignIn(authDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(login);
    }

    @GetMapping("profile/{uid}") // 회원번호로 User 프로필 조회
    public ResponseEntity<AuthDto> readUserById(@PathVariable("uid") String uid) {
        AuthDto readUser = authService.ReadUserByUid(uid);
        return ResponseEntity.status(HttpStatus.OK).body(readUser);
    }
    @PostMapping("/patch/check") // 회원정보 수정시 비밀번호 확인 페이지
    public ResponseEntity<?> checkPassword(@RequestBody AuthDto authDto) {
        if (authService.checkPassword(authDto)) { // checkPassword 가 TRUE 이면
            return ResponseEntity.ok().build(); // HTTP 200 반환
        } else { // FALSE 이면
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // HTTP 401 반환
        }
    }
    @PatchMapping("/patch/{uid}") // 회원정보 수정시 토큰 재발급으로 변경
    public ResponseEntity<TokenDto> patchUser(@PathVariable("uid") String uid,
                                             @RequestBody AuthDto authDto) {
        TokenDto tokenDto = authService.PatchUser(uid, authDto);
        return ResponseEntity.status(HttpStatus.OK).body(tokenDto); // HTTP 200 반환
    }
    @DeleteMapping("/delete/{uid}")
    public ResponseEntity<AuthDto> deleteUser(@PathVariable("uid") String uid) {
        AuthDto deleteUser = authService.DeleteUser(uid);
        return ResponseEntity.status(HttpStatus.OK).body(deleteUser); // HTTP 200 반환
    }
    @GetMapping("/mbti/{nickname}")
    public ResponseEntity<String> getCurrentUserMbti(@PathVariable("nickname") String nickname) {
        String mbti = authService.getCurrentUserMbti(nickname);
        if (mbti != null) {
            return ResponseEntity.ok(mbti);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("MBTI가 없습니다");
        }
    }


    //이 뒤로 다 Feign Client

    //Message Server에서 사용하는 Feign Client
    @GetMapping("/id/{nickname}")
    public AuthResponseDto findIdByNickname(@PathVariable("nickname") String nickname) {
        logger.info("Auth Feign 호출");
        return authService.findByNickname(nickname);
    }
    //Message Server에서 사용하는 Feign Client
    @GetMapping("/nickname/{id}")
    public AuthResponseDto findNicknameById(@PathVariable("id") Long id) {
        logger.info("Auth Feign 호출");
        return authService.findById(id);
    }
    @GetMapping("/{uid}")
    public AuthResponseDto findByUid(@PathVariable("uid") String uid) {
        logger.info("Auth Feign 호출");
        return authService.findByUid(uid);
    }
    @PostMapping("/plus/point/{id}")
    void plusPopularPoint(@PathVariable Long id){
        authService.plusPopularPoint(id);
    }
    @GetMapping("/fcm/{category}")
    public List<AuthResponseDto> getUsersByCategory(@PathVariable Category category){
        return authService.getUsersByCategory(category);
    }
    @GetMapping("/check/phoneNum")
    public AuthResponseDto checkPhoneNum(@RequestParam String phoneNum){
        return authService.checkPhoneNum(phoneNum);
    }

}
