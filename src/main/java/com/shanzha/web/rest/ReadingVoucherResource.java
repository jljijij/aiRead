package com.shanzha.web.rest;

import com.shanzha.domain.ReadingVoucher;
import com.shanzha.domain.enumeration.CouponType;
import com.shanzha.service.ReadingVoucherService;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for managing {@link ReadingVoucher}.
 */
@RestController
@RequestMapping("/api/reading-vouchers")
public class ReadingVoucherResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReadingVoucherResource.class);

    private final ReadingVoucherService readingVoucherService;

    public ReadingVoucherResource(ReadingVoucherService readingVoucherService) {
        this.readingVoucherService = readingVoucherService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ReadingVoucher> upload(
        @RequestParam("file") MultipartFile file,
        @RequestParam("type") CouponType type,
        @RequestParam(value = "novelId", required = false) Long novelId,
        @RequestParam(value = "chapterId", required = false) Long chapterId,
        @RequestParam(value = "packageId", required = false) Long packageId
    ) throws IOException {
        LOG.debug("REST request to upload file : {}", file.getOriginalFilename());
        ReadingVoucher result = readingVoucherService.saveWithFile(file, type, novelId, chapterId, packageId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/claim")
    public ResponseEntity<ReadingVoucher> claim(Principal principal) {
        LOG.debug("REST request to claim voucher by : {}", principal.getName());
        Optional<ReadingVoucher> voucher = readingVoucherService.claimVoucher(principal.getName());
        return voucher.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/available")
    public List<ReadingVoucher> getAvailable() {
        return readingVoucherService.findAvailable();
    }

    @GetMapping("/my")
    public List<ReadingVoucher> getMine(Principal principal) {
        return readingVoucherService.findByUser(principal.getName());
    }

    @GetMapping
    public List<ReadingVoucher> getAll() {
        return readingVoucherService.findAll();
    }
}
