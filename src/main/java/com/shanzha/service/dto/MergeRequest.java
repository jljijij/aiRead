package com.shanzha.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MergeRequest {
    private String fileHash;
    private String fileName;
    private String chapterName;
    private Long novelId;
    private Integer totalChunks;
}
