package com.shanzha.service.mapper;

import com.shanzha.domain.UserAiHistory;
import com.shanzha.service.dto.UserAiHistoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserAiHistory} and its DTO {@link UserAiHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserAiHistoryMapper extends EntityMapper<UserAiHistoryDTO, UserAiHistory> {}
