package com.example.smsserver.Config;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsCertificationUtil {

    @Value("${coolsms.apikey}")
    private String apiKey;

    @Value("${coolsms.apisecret}")
    private String apiSecret;

    @Value("${coolsms.fromnumber}")
    private String fromNumber;

    DefaultMessageService messageService;

    @PostConstruct
    public void init(){
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey,apiSecret,"https://api.coolsms.co.kr");
    }
    //단일 메시지 발송
    public void sendSMS(String to, String certificationCode){
        Message message = new Message();
        //휴대폰 번호는 반드시 01012345678 형태로 입력
        message.setFrom(fromNumber);
        message.setTo(to);
        message.setText("[Do표] 본인확인 인증번호는 "+certificationCode+"입니다.");

        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }
}
