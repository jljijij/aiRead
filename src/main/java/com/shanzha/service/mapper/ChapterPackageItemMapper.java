package com.shanzha.service.mapper;

import com.shanzha.domain.ChapterPackageItem;
import com.shanzha.service.dto.ChapterPackageItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChapterPackageItem} and its DTO {@link ChapterPackageItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChapterPackageItemMapper extends EntityMapper<ChapterPackageItemDTO, ChapterPackageItem> {}
