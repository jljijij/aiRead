package com.shanzha.repository;

import com.shanzha.domain.UserFoot;
import com.shanzha.service.dto.SimpleUserInfoDTO;
import com.shanzha.service.dto.UserFootStatisticDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserFoot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserFootRepository extends JpaRepository<UserFoot, Long> {
    Optional<UserFoot> findByDocumentIdAndDocumentTypeAndUserId(Long documentId, Integer documentType, Long userId);

    @Query("SELECT uf.documentId FROM UserFoot uf WHERE uf.userId = :userId AND uf.documentType = 1 AND uf.readStat = 1")
    Page<Long> findReadArticleIdsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT uf.documentId FROM UserFoot uf WHERE uf.userId = :userId AND uf.documentType = 1 AND uf.readStat = 1")
    Page<Long> findCollectedArticleIdsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(
        value = "SELECT new com.shanzha.service.dto.SimpleUserInfoDTO(u.id, u.login, u.imageUrl) " +
        "FROM UserFoot uf JOIN User u ON uf.userId = u.id " +
        "WHERE uf.documentId = :articleId AND uf.documentType = :code AND uf.praiseStat = 1",
        countQuery = "SELECT count(uf) FROM UserFoot uf WHERE uf.documentId = :articleId AND uf.documentType = :code AND uf.praiseStat = 1"
    )
    Page<SimpleUserInfoDTO> findPraisedUsers(@Param("articleId") Long articleId, @Param("code") Integer code, Pageable pageable);

    @Query(
        """
            SELECT new com.shanzha.service.dto.UserFootStatisticDTO(
                SUM(COALESCE(f.praiseStat, 0)),
                SUM(COALESCE(f.readStat, 0)),
                SUM(COALESCE(f.collectionStat, 0)),
                SUM(COALESCE(f.commentStat, 0))
            )
            FROM UserFoot f
        """
    )
    UserFootStatisticDTO getFootCount();

    Long countByDocumentIdAndDocumentTypeAndPraiseStat(Long documentId, Integer documentType, Integer praiseStat);

    // 1. 查询用户所有点赞记录
    List<UserFoot> findByUserIdAndPraiseStatAndDocumentType(Long userId, Integer praiseStat, Integer documentType);

    // 2. 查询用户所有收藏记录
    List<UserFoot> findByUserIdAndCollectionStatAndDocumentType(Long userId, Integer collectionStat, Integer documentType);

    // 3. 查询某个文档上的用户行为记录
    Optional<UserFoot> findByUserIdAndDocumentIdAndDocumentType(Long userId, Long documentId, Integer documentType);

    @Query("select uf.userId from UserFoot uf where uf.documentId = :articleId and uf.documentType = :docType and uf.collectionStat = 1")
    List<Long> findCollectorUserIds(@Param("articleId") Long articleId, @Param("docType") Integer docType);
}
