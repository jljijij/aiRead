package com.shanzha.repository;

import com.shanzha.domain.Novel;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Novel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NovelRepository extends JpaRepository<Novel, Long> {

    // 根据分类 ID 查询小说
    List<Novel> findByCategoryId(Integer categoryId);

    // 根据作者 ID 查询小说（当前登录用户）
    List<Novel> findByAuthorId(Long authorId);

    // 可选：根据分类+是否VIP查询（举例扩展用法）
    List<Novel> findByCategoryIdAndIsVip(Integer categoryId, Boolean isVip);
}
