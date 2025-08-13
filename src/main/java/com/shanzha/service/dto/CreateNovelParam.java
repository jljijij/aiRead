package com.shanzha.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateNovelParam {
    private String novelName;
    /**
     * 分类ID
     */
    private Integer category_id;

    /**
     * 标签，逗号分隔
     */
    private String tags;
    private String description;

}
