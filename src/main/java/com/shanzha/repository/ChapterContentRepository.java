package com.shanzha.repository;

import com.shanzha.domain.ChapterContent;
import java.util.List;
import java.util.Optional;

import com.shanzha.service.dto.RedisContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ChapterContent entity.
 */
@Repository
public interface ChapterContentRepository extends JpaRepository<ChapterContent, Long> {
    default Optional<ChapterContent> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ChapterContent> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ChapterContent> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select chapterContent from ChapterContent chapterContent left join fetch chapterContent.chapter",
        countQuery = "select count(chapterContent) from ChapterContent chapterContent"
    )
    Page<ChapterContent> findAllWithToOneRelationships(Pageable pageable);

    @Query("select chapterContent from ChapterContent chapterContent left join fetch chapterContent.chapter")
    List<ChapterContent> findAllWithToOneRelationships();

    @Query("select chapterContent from ChapterContent chapterContent left join fetch chapterContent.chapter where chapterContent.id =:id")
    Optional<ChapterContent> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("select chapterContent from ChapterContent chapterContent " +
        "left join fetch chapterContent.chapter " +
        "where chapterContent.novelId = :novelId " +
        "and chapterContent.chapter.id = :chapterId " +
        "and chapterContent.pageId = :pageId")
    Optional<ChapterContent> findByNovelIdAndChapterIdAndPageId(
        @Param("novelId") Long novelId,
        @Param("chapterId") Long chapterId,
        @Param("pageId") Long pageId
    );

    @Query("SELECT new com.shanzha.service.dto.RedisContent(c.novelId, c.chapter.id, c.pageId, c.compressed) " +
        "FROM ChapterContent c " +
        "WHERE c.novelId = :novelId " +
        "ORDER BY c.chapter.id ASC, c.pageId ASC")
    Page<RedisContent> findByNovelId(@Param("novelId") Long novelId, Pageable pageable);
    interface ChapterKeyView {
        Long getNovelId();
        Long getChapterId();
        Long getPageId();
    }

    @Query("""
        select cc.novelId as novelId, ch.id as chapterId, cc.pageId as pageId
        from ChapterContent cc
        join cc.chapter ch
    """)
    List<ChapterKeyView> findAllChapterKeys();
    List<ChapterContent> findByNovelIdAndChapterId(Long novelId, Long chapterId);
}
