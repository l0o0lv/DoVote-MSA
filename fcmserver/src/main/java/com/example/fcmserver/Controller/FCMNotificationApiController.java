package com.example.fcmserver.Controller;

import com.example.fcmserver.Dto.FCMNotificationRequestDto;
import com.example.fcmserver.Service.FCMNotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/fcm")
public class FCMNotificationApiController {
    private final FCMNotificationServiceImpl fcmNotificationService;
    //푸시알림 보내기
    @PostMapping("/send")
    public String sendNotificationByToken(@RequestBody FCMNotificationRequestDto requestDto){
        return fcmNotificationService.sendNotificationByCategory(requestDto);
    }

    //firebaseToken 삭제 (로그아웃 시)
//    @DeleteMapping("/{uid}/firebaseToken")
//    public ResponseEntity<Void> deleteUserFirebaseToken(@PathVariable String uid) {
//            fcmNotificationService.deleteUserFirebaseToken(uid);
//            return ResponseEntity.noContent().build();
//    }





}
