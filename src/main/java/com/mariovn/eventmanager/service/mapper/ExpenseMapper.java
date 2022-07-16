package com.mariovn.eventmanager.service.mapper;


import com.mariovn.eventmanager.domain.*;
import com.mariovn.eventmanager.service.dto.ExpenseDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Expense} and its DTO {@link ExpenseDTO}.
 */
@Mapper(componentModel = "spring", uses = {EventMapper.class, ParticipantMapper.class})
public interface ExpenseMapper extends EntityMapper<ExpenseDTO, Expense> {

    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "participant.id", target = "participantId")
    ExpenseDTO toDto(Expense expense);

    @Mapping(source = "eventId", target = "event")
    @Mapping(source = "participantId", target = "participant")
    Expense toEntity(ExpenseDTO expenseDTO);

    default Expense fromId(Long id) {
        if (id == null) {
            return null;
        }
        Expense expense = new Expense();
        expense.setId(id);
        return expense;
    }
}
