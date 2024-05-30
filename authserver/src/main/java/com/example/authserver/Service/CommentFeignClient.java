package com.example.authserver.Service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "comment-server", path = "/comments")
public interface CommentFeignClient {

    @GetMapping("/count/{userId}")
    Long countByUserId(@PathVariable Long userId);
}
