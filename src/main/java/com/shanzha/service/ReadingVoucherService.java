package com.shanzha.service;

import com.shanzha.domain.ReadingVoucher;
import com.shanzha.domain.enumeration.CouponType;
import com.shanzha.repository.ReadingVoucherRepository;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
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

    private final StringRedisTemplate stringRedisTemplate;

    private DefaultRedisScript<Long> claimScript;

    private static final String CLAIM_KEY_PREFIX = "voucher:claim:";
    private static final long DAILY_LIMIT = 1000L;

    public ReadingVoucherService(ReadingVoucherRepository readingVoucherRepository, StringRedisTemplate stringRedisTemplate) {
        this.readingVoucherRepository = readingVoucherRepository;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostConstruct
    public void init() {
        String script =
            "local c = redis.call('get', KEYS[1])\n" +
            "if c and tonumber(c) >= tonumber(ARGV[1]) then return 0 end\n" +
            "c = redis.call('incr', KEYS[1])\n" +
            "if tonumber(c) == 1 then redis.call('expire', KEYS[1], 86400) end\n" +
            "return 1";
        claimScript = new DefaultRedisScript<>();
        claimScript.setScriptText(script);
        claimScript.setResultType(Long.class);
    }

    public ReadingVoucher saveWithFile(MultipartFile file, CouponType type, Long novelId, Long chapterId, Long packageId)
        throws IOException {
        ReadingVoucher voucher = new ReadingVoucher();
        voucher.setCode(UUID.randomUUID().toString());
        voucher.setIssuedAt(Instant.now());
        voucher.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        voucher.setType(type);
        voucher.setNovelId(novelId);
        voucher.setChapterId(chapterId);
        voucher.setPackageId(packageId);
        voucher.setFile(file.getBytes());
        voucher.setFileContentType(file.getContentType());
        return readingVoucherRepository.save(voucher);
    }

    public Optional<ReadingVoucher> claimVoucher(String userLogin) {
        String key = CLAIM_KEY_PREFIX + LocalDate.now();
        Long allowed = stringRedisTemplate.execute(claimScript, Collections.singletonList(key), String.valueOf(DAILY_LIMIT));
        if (allowed == null || allowed == 0L) {
            return Optional.empty();
        }

        for (int i = 0; i < 3; i++) {
            Optional<ReadingVoucher> opt = readingVoucherRepository.findFirstByClaimedByIsNullOrderByIssuedAtAsc();
            if (opt.isEmpty()) {
                return Optional.empty();
            }
            ReadingVoucher voucher = opt.get();
            voucher.setClaimedBy(userLogin);
            try {
                readingVoucherRepository.saveAndFlush(voucher);
                return Optional.of(voucher);
            } catch (OptimisticLockingFailureException e) {
                // retry
            }
        }
        return Optional.empty();
    }

    @Scheduled(fixedRate = 86_400_000)
    public void issuePeriodicVoucher() {
        issuePeriodicVoucher(null, null, null, null);
    }

    public void issuePeriodicVoucher(CouponType type, Long novelId, Long chapterId, Long packageId) {
        ReadingVoucher voucher = new ReadingVoucher();
        voucher.setCode(UUID.randomUUID().toString());
        voucher.setIssuedAt(Instant.now());
        voucher.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        voucher.setType(type);
        voucher.setNovelId(novelId);
        voucher.setChapterId(chapterId);
        voucher.setPackageId(packageId);
        readingVoucherRepository.save(voucher);
    }

    @Transactional(readOnly = true)
    public List<ReadingVoucher> findAll() {
        return readingVoucherRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ReadingVoucher> findAvailable() {
        return readingVoucherRepository.findByClaimedByIsNull();
    }

    @Transactional(readOnly = true)
    public List<ReadingVoucher> findByUser(String userLogin) {
        return readingVoucherRepository.findByClaimedBy(userLogin);
    }
}
