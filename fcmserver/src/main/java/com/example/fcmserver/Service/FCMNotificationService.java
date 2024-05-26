package com.example.fcmserver.Service;

import com.example.fcmserver.Dto.FCMNotificationRequestDto;

public  interface FCMNotificationService {

    //푸시 알림 보내기
    String sendNotificationByCategory(FCMNotificationRequestDto requestDto);

    //firebaseToken 삭제(로그아웃 시)
//    String deleteUserFirebaseToken(String uid);
}
