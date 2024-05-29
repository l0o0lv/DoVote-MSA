package com.example.smsserver.Repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class SmsRepository {
    private final String PREFIX = "sms:";

    private final StringRedisTemplate stringRedisTemplate;

    public void createSmsCertification(String phone, String code){
        int LIMIT_TIME = 3 * 60;
        stringRedisTemplate.opsForValue()
                .set(PREFIX + phone, code, Duration.ofSeconds(LIMIT_TIME));
    }

    public String getSmsCertification(String phone){
        return stringRedisTemplate.opsForValue().get(PREFIX + phone);
    }

    public void deleteSmsCertification(String phone){
        stringRedisTemplate.delete(PREFIX + phone);
    }

    public boolean hasKey(String phone){
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(PREFIX + phone));
    }


}
