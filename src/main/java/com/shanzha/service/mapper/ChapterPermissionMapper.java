package com.shanzha.service.mapper;

import com.shanzha.domain.ChapterPermission;
import com.shanzha.service.dto.ChapterPermissionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChapterPermission} and its DTO {@link ChapterPermissionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChapterPermissionMapper extends EntityMapper<ChapterPermissionDTO, ChapterPermission> {}
