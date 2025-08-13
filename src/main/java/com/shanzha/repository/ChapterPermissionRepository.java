package com.shanzha.repository;

import com.shanzha.domain.ChapterPermission;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ChapterPermission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChapterPermissionRepository extends JpaRepository<ChapterPermission, Long> {}
