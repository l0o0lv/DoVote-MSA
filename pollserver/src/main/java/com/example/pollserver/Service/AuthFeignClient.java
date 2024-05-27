package com.example.pollserver.Service;

import com.example.pollserver.Dto.Feign.AuthResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "auth-server", path = "/auth")
public interface AuthFeignClient {
    @GetMapping("/id/{nickname}")
    AuthResponseDto findByNickname(@PathVariable String nickname);

    @GetMapping("/nickname/{id}")
    AuthResponseDto findById(@PathVariable Long id);

    @PostMapping("/plus/point/{id}")
    void plusPopularPoint(@PathVariable Long id);
}
