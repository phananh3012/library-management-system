package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PatronAccountDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PatronAccountDTO.class);
        PatronAccountDTO patronAccountDTO1 = new PatronAccountDTO();
        patronAccountDTO1.setId(1L);
        PatronAccountDTO patronAccountDTO2 = new PatronAccountDTO();
        assertThat(patronAccountDTO1).isNotEqualTo(patronAccountDTO2);
        patronAccountDTO2.setId(patronAccountDTO1.getId());
        assertThat(patronAccountDTO1).isEqualTo(patronAccountDTO2);
        patronAccountDTO2.setId(2L);
        assertThat(patronAccountDTO1).isNotEqualTo(patronAccountDTO2);
        patronAccountDTO1.setId(null);
        assertThat(patronAccountDTO1).isNotEqualTo(patronAccountDTO2);
    }
}
