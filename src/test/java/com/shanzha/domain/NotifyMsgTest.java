package com.shanzha.domain;

import static com.shanzha.domain.NotifyMsgTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotifyMsgTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotifyMsg.class);
        NotifyMsg notifyMsg1 = getNotifyMsgSample1();
        NotifyMsg notifyMsg2 = new NotifyMsg();
        assertThat(notifyMsg1).isNotEqualTo(notifyMsg2);

        notifyMsg2.setId(notifyMsg1.getId());
        assertThat(notifyMsg1).isEqualTo(notifyMsg2);

        notifyMsg2 = getNotifyMsgSample2();
        assertThat(notifyMsg1).isNotEqualTo(notifyMsg2);
    }
}
