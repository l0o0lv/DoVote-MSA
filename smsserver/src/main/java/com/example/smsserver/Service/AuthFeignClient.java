package com.example.smsserver.Service;

import com.example.smsserver.Dto.AuthResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-server", path = "/auth")
public interface AuthFeignClient {
    @GetMapping("/check/phoneNum")
    AuthResponseDto checkPhoneNum(@RequestParam String phoneNum);
}
