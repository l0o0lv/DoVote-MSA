package com.example.fcmserver.Service;

import com.example.fcmserver.Dto.AuthResponseDto;
import com.example.fcmserver.Enum.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "auth-server", path = "/auth")
public interface AuthFeignClient {
    @GetMapping("/fcm/{category}")
    List<AuthResponseDto> getUsersByCategory(@PathVariable Category category);
}
