package com.shanzha.service.mapper;

import com.shanzha.domain.NovelTag;
import com.shanzha.service.dto.NovelTagDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link NovelTag} and its DTO {@link NovelTagDTO}.
 */
@Mapper(componentModel = "spring")
public interface NovelTagMapper extends EntityMapper<NovelTagDTO, NovelTag> {}
