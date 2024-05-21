package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HoldDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HoldDTO.class);
        HoldDTO holdDTO1 = new HoldDTO();
        holdDTO1.setId(1L);
        HoldDTO holdDTO2 = new HoldDTO();
        assertThat(holdDTO1).isNotEqualTo(holdDTO2);
        holdDTO2.setId(holdDTO1.getId());
        assertThat(holdDTO1).isEqualTo(holdDTO2);
        holdDTO2.setId(2L);
        assertThat(holdDTO1).isNotEqualTo(holdDTO2);
        holdDTO1.setId(null);
        assertThat(holdDTO1).isNotEqualTo(holdDTO2);
    }
}
