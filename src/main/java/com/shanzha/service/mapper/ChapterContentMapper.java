package com.shanzha.service.mapper;

import com.shanzha.domain.Chapter;
import com.shanzha.domain.ChapterContent;
import com.shanzha.service.dto.ChapterContentDTO;
import com.shanzha.service.dto.ChapterDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChapterContent} and its DTO {@link ChapterContentDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChapterContentMapper extends EntityMapper<ChapterContentDTO, ChapterContent> {
    @Mapping(target = "chapter", source = "chapter", qualifiedByName = "chapterTitle")
    ChapterContentDTO toDto(ChapterContent s);

    @Named("chapterTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    ChapterDTO toDtoChapterTitle(Chapter chapter);
}
