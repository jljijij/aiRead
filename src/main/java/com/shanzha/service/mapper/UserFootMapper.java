package com.shanzha.service.mapper;

import com.shanzha.domain.UserFoot;
import com.shanzha.service.dto.UserFootDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserFoot} and its DTO {@link UserFootDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserFootMapper extends EntityMapper<UserFootDTO, UserFoot> {}
