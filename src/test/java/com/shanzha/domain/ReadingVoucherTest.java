package com.shanzha.domain;

import static com.shanzha.domain.ReadingVoucherTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReadingVoucherTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReadingVoucher.class);
        ReadingVoucher voucher1 = getReadingVoucherSample1();
        ReadingVoucher voucher2 = new ReadingVoucher();
        assertThat(voucher1).isNotEqualTo(voucher2);

        voucher2.setId(voucher1.getId());
        assertThat(voucher1).isEqualTo(voucher2);

        voucher2 = getReadingVoucherSample2();
        assertThat(voucher1).isNotEqualTo(voucher2);
    }
}
