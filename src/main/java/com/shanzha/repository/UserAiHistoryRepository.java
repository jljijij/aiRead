package com.shanzha.repository;

import com.shanzha.domain.UserAiHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserAiHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserAiHistoryRepository extends JpaRepository<UserAiHistory, Long> {}
