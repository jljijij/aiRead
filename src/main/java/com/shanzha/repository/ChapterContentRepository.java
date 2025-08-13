package com.shanzha.repository;

import com.shanzha.domain.ChapterContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ChapterContent entity.
 */
@Repository
public interface ChapterContentRepository extends JpaRepository<ChapterContent, Long> {}
