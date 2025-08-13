package com.shanzha.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChapterUpdate implements Serializable {
    private Long novelId;
    private Long chapterId;
}
