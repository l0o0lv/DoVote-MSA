package com.example.pollserver.Dto.Poll;

import com.example.pollserver.Enum.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PollRequest {

    private Long userId; // user -> userid 변경
    private String createdBy; // 추가
    @NotBlank
    private String title;

    @NotBlank
    private String question;
    private Category category;
    private String mediaName;
    private String mediaUrl;


//    public PollDto toDto() {
//        PollDto pollDto = new PollDto();
//        pollDto.setTitle(this.title);
//        pollDto.setQuestion(this.question);
//        pollDto.setCategory(this.category);
//        pollDto.setCreatedBy(this.userId);
//        pollDto.setUserId(this.userId);
//
//        return pollDto;
//    }


}