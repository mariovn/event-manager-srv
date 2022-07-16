package com.mariovn.eventmanager.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.mariovn.eventmanager.web.rest.TestUtil;

public class ExpenseDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExpenseDTO.class);
        ExpenseDTO expenseDTO1 = new ExpenseDTO();
        expenseDTO1.setId(1L);
        ExpenseDTO expenseDTO2 = new ExpenseDTO();
        assertThat(expenseDTO1).isNotEqualTo(expenseDTO2);
        expenseDTO2.setId(expenseDTO1.getId());
        assertThat(expenseDTO1).isEqualTo(expenseDTO2);
        expenseDTO2.setId(2L);
        assertThat(expenseDTO1).isNotEqualTo(expenseDTO2);
        expenseDTO1.setId(null);
        assertThat(expenseDTO1).isNotEqualTo(expenseDTO2);
    }
}
