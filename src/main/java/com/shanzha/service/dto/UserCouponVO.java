package com.shanzha.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCouponVO implements Serializable {
    private Long id;
    private String code;
    private String novelTitle;
    private String chapterTitle;
    private Instant expiredAt;
    private Boolean isExpired;
    private Instant usedAt;

}
