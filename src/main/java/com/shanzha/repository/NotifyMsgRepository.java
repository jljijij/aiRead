package com.shanzha.repository;

import com.shanzha.domain.NotifyMsg;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the NotifyMsg entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotifyMsgRepository extends JpaRepository<NotifyMsg, Long> {}
