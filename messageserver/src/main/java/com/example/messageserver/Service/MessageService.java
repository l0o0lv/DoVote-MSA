package com.example.messageserver.Service;

import com.example.messageserver.Dto.MessageDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {
    MessageDto SendMessage(MessageDto messageDto);

    List<MessageDto> ReadAllMessage(String nickname);

    MessageDto ReadMessage(Long messageId);

    String CountMessage(String nickname);

    void DeleteMessage(Long messageId);
}
