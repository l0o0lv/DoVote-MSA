package com.example.commentserver.Service.Feign;

import com.example.commentserver.Dto.AuthResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-server", path = "/auth")
public interface AuthFeignClient {
    @GetMapping("/id/{nickname}")
    AuthResponseDto findByNickname(@PathVariable String nickname);

    @GetMapping("/nickname/{id}")
    AuthResponseDto findById(@PathVariable Long id);

    @GetMapping("/{uid}")
    AuthResponseDto findByUid(@PathVariable String uid);
}
