package com.shanzha.repository;

import com.shanzha.domain.ReadingVoucher;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReadingVoucher entity.
 */
@Repository
public interface ReadingVoucherRepository extends JpaRepository<ReadingVoucher, Long> {
    @Lock(LockModeType.OPTIMISTIC)
    Optional<ReadingVoucher> findFirstByClaimedByIsNullOrderByIssuedAtAsc();
}
