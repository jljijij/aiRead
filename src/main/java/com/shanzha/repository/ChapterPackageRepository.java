package com.shanzha.repository;

import com.shanzha.domain.ChapterPackage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ChapterPackage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChapterPackageRepository extends JpaRepository<ChapterPackage, Long> {}
