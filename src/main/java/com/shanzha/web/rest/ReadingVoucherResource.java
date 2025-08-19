package com.shanzha.web.rest;

import com.shanzha.domain.ReadingVoucher;
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
    public ResponseEntity<ReadingVoucher> upload(@RequestParam("file") MultipartFile file) throws IOException {
        LOG.debug("REST request to upload file : {}", file.getOriginalFilename());
        ReadingVoucher result = readingVoucherService.saveWithFile(file);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/claim")
    public ResponseEntity<ReadingVoucher> claim(Principal principal) {
        LOG.debug("REST request to claim voucher by : {}", principal.getName());
        Optional<ReadingVoucher> voucher = readingVoucherService.claimVoucher(principal.getName());
        return voucher.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ReadingVoucher> getAll() {
        return readingVoucherService.findAll();
    }
}
