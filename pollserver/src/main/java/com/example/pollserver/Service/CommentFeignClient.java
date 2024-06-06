package com.example.pollserver.Service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "comment-server", path = "/comments")
public interface CommentFeignClient {
    @DeleteMapping("/cascade/{pollId}")
    void deleteComments(@PathVariable Long pollId);
}
