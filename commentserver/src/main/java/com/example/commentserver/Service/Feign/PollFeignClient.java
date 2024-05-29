package com.example.commentserver.Service.Feign;

import com.example.commentserver.Dto.PollResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "poll-server", path = "/polls")
public interface PollFeignClient {
    @GetMapping("/find/{id}")
    PollResponseDto findById(@PathVariable Long id);
}
