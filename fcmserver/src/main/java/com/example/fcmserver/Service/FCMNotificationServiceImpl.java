package com.example.fcmserver.Service;

import com.example.fcmserver.Dto.AuthResponseDto;
import com.example.fcmserver.Dto.FCMNotificationRequestDto;
import com.example.fcmserver.Enum.Category;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class FCMNotificationServiceImpl implements FCMNotificationService{

    private final FirebaseMessaging firebaseMessaging;
    private final AuthFeignClient authFeignClient;

    public FCMNotificationServiceImpl(@Autowired FirebaseMessaging firebaseMessaging,
                                      AuthFeignClient authFeignClient) {
        this.firebaseMessaging = firebaseMessaging;
        this.authFeignClient = authFeignClient;
    }

    //관심 카테고리별로 푸시알림 보내기
    public String sendNotificationByCategory(FCMNotificationRequestDto requestDto) {

        List<AuthResponseDto> users = authFeignClient.getUsersByCategory(Category.valueOf(requestDto.getCategory()));

        if (!users.isEmpty()) {
            Notification notification = Notification.builder()
                    .setTitle(requestDto.getTitle())
                    .setBody(requestDto.getBody())
                    .build();

            for (AuthResponseDto user : users) {
                if (user.getFirebaseToken() != null) {
                    Message message = Message.builder()
                            .setToken(user.getFirebaseToken())
                            .setNotification(notification)
                            .putData("category", requestDto.getCategory()) // 추가 데이터 필드
                            .putData("title", requestDto.getTitle())
                            .putData("body", requestDto.getBody())
                            .build();

                    try {
                        firebaseMessaging.send(message);
                    } catch (FirebaseMessagingException e) {
                        e.printStackTrace();
                        // 알림 전송 실패 처리
                    }
                }
            }

            return "관심 카테고리 사용자들에게 알림을 성공적으로 전송했습니다.";
        } else {
            return "해당 카테고리에 관심 있는 사용자가 없습니다.";
        }


    }
//    public String deleteUserFirebaseToken(String uid) {
//        UserEntity user = userRepository.findByUid(uid);
//        if (user != null) {
//            user.setFirebaseToken(null); // Firebase Token 삭제
//            userRepository.save(user); // 변경사항 저장
//
//            return "firebaseToken을 삭제하였습니다. ";
//        } else {
//            return "해당 user가 없습니다. ";
//        }
//    }

}
