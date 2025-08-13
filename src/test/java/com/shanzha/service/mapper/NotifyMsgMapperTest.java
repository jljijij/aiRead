package com.shanzha.service.mapper;

import static com.shanzha.domain.NotifyMsgAsserts.*;
import static com.shanzha.domain.NotifyMsgTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotifyMsgMapperTest {

    private NotifyMsgMapper notifyMsgMapper;

    @BeforeEach
    void setUp() {
        notifyMsgMapper = new NotifyMsgMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNotifyMsgSample1();
        var actual = notifyMsgMapper.toEntity(notifyMsgMapper.toDto(expected));
        assertNotifyMsgAllPropertiesEquals(expected, actual);
    }
}
