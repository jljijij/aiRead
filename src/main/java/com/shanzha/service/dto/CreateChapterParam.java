package com.shanzha.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChapterParam {
    private String chapterTitle;
    private Integer chapterNum;
    private Long novelId;
}
