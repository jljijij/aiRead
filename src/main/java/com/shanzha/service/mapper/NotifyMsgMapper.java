package com.shanzha.service.mapper;

import com.shanzha.domain.NotifyMsg;
import com.shanzha.service.dto.NotifyMsgDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link NotifyMsg} and its DTO {@link NotifyMsgDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotifyMsgMapper extends EntityMapper<NotifyMsgDTO, NotifyMsg> {}
