package com.example.fcmserver.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FCMNotificationRequestDto {
    private String category;
    private String title;
    private String body;

    @Builder
    public FCMNotificationRequestDto(String category, String title, String body){
        this.category = category;
        this.body = body;
        this.title = title;
    }
}
