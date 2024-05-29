package com.example.smsserver.Service;

import com.example.smsserver.Config.SmsCertificationUtil;
import com.example.smsserver.Dto.AuthResponseDto;
import com.example.smsserver.Dto.SmsRequestDto;
import com.example.smsserver.Dto.SmsVerifyDto;
import com.example.smsserver.Repository.SmsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService{
    private final static Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    private final SmsCertificationUtil smsCertificationUtil;
    private final SmsRepository smsRepository;
    private final AuthFeignClient authFeignClient;

    public SmsServiceImpl(@Autowired SmsCertificationUtil smsCertificationUtil,
                          AuthFeignClient authFeignClient,
                          SmsRepository smsRepository){
        this.smsCertificationUtil = smsCertificationUtil;
        this.smsRepository = smsRepository;
        this.authFeignClient = authFeignClient;
    }

    @Override
    public void SendSms(SmsRequestDto smsRequestDto) {
        String phoneNum = smsRequestDto.getPhoneNum();

        AuthResponseDto authResponseDto = authFeignClient.checkPhoneNum(phoneNum);

        if(authResponseDto != null){
            throw new IllegalArgumentException("이미 가입된 번호입니다.");
        }

        String certificationCode = Integer.toString((int)(Math.random() * (999999 - 100000 + 1)) + 100000);
        smsCertificationUtil.sendSMS(phoneNum, certificationCode);
        smsRepository.createSmsCertification(phoneNum, certificationCode);
    }

    @Override
    public boolean verifyCode(SmsVerifyDto smsVerifyDto) {
        if(isVerify(smsVerifyDto.getPhoneNum(), smsVerifyDto.getCertificationCode())){
            smsRepository.deleteSmsCertification(smsVerifyDto.getPhoneNum());
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isVerify(String phoneNum, String certificationCode){
        return smsRepository.hasKey(phoneNum) &&
                smsRepository.getSmsCertification(phoneNum).equals(certificationCode);
    }
}
