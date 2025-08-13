package com.shanzha.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageStruct {
    private int chapterId;
    private String chapterTitle;
    private int totalSegments;
    private int totalWords;
    private List<Segment> segments;
    private String createTime;
    private String updateTime;

}
