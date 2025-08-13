package com.shanzha.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleUserInfoDTO {

    private Long userId;

    private String name;

    private String imageUrl;
}
