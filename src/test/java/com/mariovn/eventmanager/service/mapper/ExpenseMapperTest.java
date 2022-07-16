package com.mariovn.eventmanager.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ExpenseMapperTest {

    private ExpenseMapper expenseMapper;

    @BeforeEach
    public void setUp() {
        expenseMapper = new ExpenseMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(expenseMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(expenseMapper.fromId(null)).isNull();
    }
}
