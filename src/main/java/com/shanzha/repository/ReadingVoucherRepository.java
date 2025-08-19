package com.shanzha.repository;

import com.shanzha.domain.ReadingVoucher;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReadingVoucher entity.
 */
@Repository
public interface ReadingVoucherRepository extends JpaRepository<ReadingVoucher, Long> {
    Optional<ReadingVoucher> findFirstByClaimedByIsNullOrderByIssuedAtAsc();
}
