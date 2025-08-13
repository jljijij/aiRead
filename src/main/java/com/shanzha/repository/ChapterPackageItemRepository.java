package com.shanzha.repository;

import com.shanzha.domain.ChapterPackageItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ChapterPackageItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChapterPackageItemRepository extends JpaRepository<ChapterPackageItem, Long> {}
