package com.example.commentserver.Dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@Getter
public class ReportDto {
    private Long userId;
    private String reportReason;
}