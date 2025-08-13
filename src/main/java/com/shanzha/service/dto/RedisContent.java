package com.shanzha.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisContent {

    public Long novelId;
    public Long chapterId;
    public Long page;
    public byte[] content;
}
