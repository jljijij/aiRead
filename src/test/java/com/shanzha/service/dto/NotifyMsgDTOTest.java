package com.shanzha.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotifyMsgDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotifyMsgDTO.class);
        NotifyMsgDTO notifyMsgDTO1 = new NotifyMsgDTO();
        notifyMsgDTO1.setId(1L);
        NotifyMsgDTO notifyMsgDTO2 = new NotifyMsgDTO();
        assertThat(notifyMsgDTO1).isNotEqualTo(notifyMsgDTO2);
        notifyMsgDTO2.setId(notifyMsgDTO1.getId());
        assertThat(notifyMsgDTO1).isEqualTo(notifyMsgDTO2);
        notifyMsgDTO2.setId(2L);
        assertThat(notifyMsgDTO1).isNotEqualTo(notifyMsgDTO2);
        notifyMsgDTO1.setId(null);
        assertThat(notifyMsgDTO1).isNotEqualTo(notifyMsgDTO2);
    }
}
