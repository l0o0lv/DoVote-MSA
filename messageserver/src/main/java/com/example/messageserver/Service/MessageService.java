package com.example.messageserver.Service;

import com.example.messageserver.Dto.MessageDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {
    MessageDto SendMessage(MessageDto messageDto);

    MessageDto ReadMessage(Long messageId);

    List<MessageDto> ReadAllMessage(Long userId);

    String CountMessage(Long userId);

    void DeleteMessage(Long messageId);
}
