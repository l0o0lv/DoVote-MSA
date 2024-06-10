package com.example.messageserver.Service;

import com.example.messageserver.Dto.AuthResponseDto;
import com.example.messageserver.Dto.MessageDto;
import com.example.messageserver.Entity.MessageEntity;
import com.example.messageserver.Repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService{
    private final MessageRepository messageRepository;
    private final AuthFeignClient authFeignClient;

    public MessageServiceImpl(
            @Autowired MessageRepository messageRepository,
            @Autowired AuthFeignClient authFeignClient){ //AuthController 호출
        this.messageRepository = messageRepository;
        this.authFeignClient = authFeignClient;
    }

    public MessageDto SendMessage(MessageDto messageDto){

        if(messageDto.getSenderNickname().equals(messageDto.getReceiverNickname())){
            throw new IllegalArgumentException("자신에게 쪽지를 보낼 수 없습니다.");
        }

        AuthResponseDto senderId = authFeignClient.findByNickname(messageDto.getSenderNickname());
        AuthResponseDto receiverId = authFeignClient.findByNickname(messageDto.getReceiverNickname());

        MessageEntity messageEntity = dtoToEntity(messageDto,senderId,receiverId);
        messageRepository.save(messageEntity);

        return entityToDto(messageEntity);
    }

    @Override
    public List<MessageDto> ReadAllMessage(Long userId){
        log.info("(MessageService) ReadAllMessage 실행.");
        return messageRepository.findAllByReceiverIdOrderBySendTimeDesc(userId)
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MessageDto ReadMessage(Long messageId){
        log.info("(MessageService) ReadMessage 실행.");
        MessageEntity messageEntity = messageRepository.findById(messageId)
                .orElseThrow(()-> new IllegalArgumentException("해당 쪽지가 존재하지 않습니다."));

        messageEntity.setReadStatus(true); //메시지 조회시 읽음 여부 true로 변경
        messageRepository.save(messageEntity); //저장
        return entityToDto(messageEntity);
    }

    @Override
    public String CountMessage(Long userId){
        return messageRepository.countByReceiverIdAndReadStatus(userId, false).toString();
    }
    public void DeleteMessage(Long messageId){
        messageRepository.deleteById(messageId);
    }

    //MessageDto는 닉네임을 받아서 사용하지만, Entity는 회원번호를 사용하므로 Service에서 변환하는 메소드 생성하였음
    public MessageEntity dtoToEntity(MessageDto messageDto,AuthResponseDto sender, AuthResponseDto receiver){
        return MessageEntity.builder()
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .content(messageDto.getContent())
                .build();
    }
    public MessageDto entityToDto(MessageEntity messageEntity){
        return MessageDto.builder()
                .messageId(messageEntity.getMessageId())
                .senderNickname(String.valueOf(authFeignClient.findById((messageEntity.getSenderId())).getNickname()))
                .receiverNickname(String.valueOf(authFeignClient.findById((messageEntity.getReceiverId())).getNickname()))
                .content(messageEntity.getContent())
                .sendTime(messageEntity.getSendTime())
                .readStatus(messageEntity.isReadStatus())
                .build();
    }
}
