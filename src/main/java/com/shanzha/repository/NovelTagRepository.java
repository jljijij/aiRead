package com.shanzha.repository;

import com.shanzha.domain.NovelTag;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the NovelTag entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NovelTagRepository extends JpaRepository<NovelTag, Long> {}
