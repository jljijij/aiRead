package com.shanzha.service;

import com.shanzha.domain.ReadingVoucher;
import com.shanzha.repository.ReadingVoucherRepository;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for managing {@link ReadingVoucher}.
 */
@Service
@Transactional
public class ReadingVoucherService {

    private final ReadingVoucherRepository readingVoucherRepository;

    public ReadingVoucherService(ReadingVoucherRepository readingVoucherRepository) {
        this.readingVoucherRepository = readingVoucherRepository;
    }

    public ReadingVoucher saveWithFile(MultipartFile file) throws IOException {
        ReadingVoucher voucher = new ReadingVoucher();
        voucher.setCode(UUID.randomUUID().toString());
        voucher.setIssuedAt(Instant.now());
        voucher.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        voucher.setFile(file.getBytes());
        voucher.setFileContentType(file.getContentType());
        return readingVoucherRepository.save(voucher);
    }

    public Optional<ReadingVoucher> claimVoucher(String userLogin) {
        return readingVoucherRepository
            .findFirstByClaimedByIsNullOrderByIssuedAtAsc()
            .map(voucher -> {
                voucher.setClaimedBy(userLogin);
                return voucher;
            });
    }

    @Scheduled(fixedRate = 86_400_000)
    public void issuePeriodicVoucher() {
        ReadingVoucher voucher = new ReadingVoucher();
        voucher.setCode(UUID.randomUUID().toString());
        voucher.setIssuedAt(Instant.now());
        voucher.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        readingVoucherRepository.save(voucher);
    }

    @Transactional(readOnly = true)
    public List<ReadingVoucher> findAll() {
        return readingVoucherRepository.findAll();
    }
}
