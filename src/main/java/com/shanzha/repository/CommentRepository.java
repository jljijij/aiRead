package com.shanzha.repository;

import com.shanzha.domain.Comment;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 顶级评论分页
    @Query("SELECT c FROM Comment c WHERE c.topCommentId = 0 AND c.articleId = :articleId AND c.deleted = 0 ORDER BY c.id DESC")
    List<Comment> findTopComments(@Param("articleId") Long articleId, Pageable pageable);

    // 子评论列表
    List<Comment> findByArticleIdAndTopCommentIdInAndDeleted(Long articleId, Collection<Long> topCommentIds, Integer deleted);

    // 评论总数
    int countByArticleIdAndDeleted(Long articleId, Integer deleted);

    @Query(
        value = """
        SELECT * FROM comment
        WHERE top_comment_id = (
            SELECT top_comment_id
            FROM comment
            WHERE article_id = :articleId AND deleted = 0
            GROUP BY top_comment_id
            ORDER BY COUNT(*) DESC
            LIMIT 1
        ) AND top_comment_id = id
        LIMIT 1
        """,
        nativeQuery = true
    )
    Comment findHotTopComment(@Param("articleId") Long articleId);
}
