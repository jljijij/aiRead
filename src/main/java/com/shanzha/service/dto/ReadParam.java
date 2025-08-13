package com.shanzha.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadParam {
    private Long novelId;
    private Long ChapterId;
    private Long index;
}
