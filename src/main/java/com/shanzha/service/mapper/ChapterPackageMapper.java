package com.shanzha.service.mapper;

import com.shanzha.domain.ChapterPackage;
import com.shanzha.service.dto.ChapterPackageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChapterPackage} and its DTO {@link ChapterPackageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChapterPackageMapper extends EntityMapper<ChapterPackageDTO, ChapterPackage> {}
