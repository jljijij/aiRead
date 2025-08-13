package com.shanzha.service.mapper;

import com.shanzha.domain.Novel;
import com.shanzha.service.dto.NovelDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Novel} and its DTO {@link NovelDTO}.
 */
@Mapper(componentModel = "spring")
public interface NovelMapper extends EntityMapper<NovelDTO, Novel> {}
