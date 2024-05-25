package com.example.smsserver.Service;

import com.example.smsserver.Config.SmsCertificationUtil;
import com.example.smsserver.Dto.SmsRequestDto;
import com.example.smsserver.Dto.SmsVerifyDto;
import com.example.smsserver.Repository.SmsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService{

    private final SmsCertificationUtil smsCertificationUtil;
    private final SmsRepository smsRepository;

    public SmsServiceImpl(@Autowired SmsCertificationUtil smsCertificationUtil,
                          SmsRepository smsRepository){
        this.smsCertificationUtil = smsCertificationUtil;
        this.smsRepository = smsRepository;
    }

    @Override
    public void SendSms(SmsRequestDto smsRequestDto) {
        String phoneNum = smsRequestDto.getPhoneNum();
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
//           throw new IllegalArgumentException("인증번호가 일치하지 않습니다."); //테스트를 위해 생성한 코드
        }
    }

    public boolean isVerify(String phoneNum, String certificationCode){
        return smsRepository.hasKey(phoneNum) &&
                smsRepository.getSmsCertification(phoneNum).equals(certificationCode);
    }
}
