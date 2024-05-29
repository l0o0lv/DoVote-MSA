package com.example.smsserver.Service;

import com.example.smsserver.Dto.SmsRequestDto;
import com.example.smsserver.Dto.SmsVerifyDto;
import org.springframework.stereotype.Service;

@Service
public interface SmsService {

    void SendSms(SmsRequestDto smsRequestDto);

    boolean verifyCode(SmsVerifyDto smsVerifyDto);
}
