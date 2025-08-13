package com.shanzha.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class Segment {
    private int segId;
    private int words;
    private String content;
    private String version;
}
