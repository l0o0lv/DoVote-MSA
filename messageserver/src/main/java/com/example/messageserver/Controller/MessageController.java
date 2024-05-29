package com.example.messageserver.Controller;

import com.example.messageserver.Dto.MessageDto;
import com.example.messageserver.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;

    public MessageController(
            @Autowired MessageService messageService){
        this.messageService = messageService;
    }
    @PostMapping("/send") // 쪽지 전송
    public ResponseEntity<MessageDto> SendMessage(@RequestBody MessageDto messageDto){
        MessageDto send = messageService.SendMessage(messageDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(send);
    }

    @GetMapping("/read/all/{nickname}") // nickname의 모든 쪽지 조회
    public ResponseEntity<List<MessageDto>> ReadAllMessage(@PathVariable String nickname){
        List<MessageDto> messageList = messageService.ReadAllMessage(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(messageList);
    }

    @GetMapping("/read/{messageId}") // 쪽지 읽기 String -> Long 변경
    public ResponseEntity<MessageDto> ReadMessage(@PathVariable Long messageId){
        MessageDto readMessage = messageService.ReadMessage(messageId);
        return ResponseEntity.status(HttpStatus.OK).body(readMessage);
    }
    @GetMapping("count/{nickname}") //nickname의 읽지 않은 쪽지의 개수
    public ResponseEntity<String> CountMessage(@PathVariable String nickname){
        String count = messageService.CountMessage(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(count);
    }
    @DeleteMapping("/delete/{messageId}") // 쪽지 삭제 추가
    public void DeleteMessage(@PathVariable Long messageId){
        messageService.DeleteMessage(messageId);
    }
}