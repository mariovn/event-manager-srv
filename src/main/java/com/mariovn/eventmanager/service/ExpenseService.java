package com.mariovn.eventmanager.service;

import com.mariovn.eventmanager.domain.enumeration.CurrencyType;
import com.mariovn.eventmanager.service.dto.EventDTO;
import com.mariovn.eventmanager.service.dto.ExpenseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.mariovn.eventmanager.domain.Expense}.
 */
public interface ExpenseService {

    /**
     * Save a expense.
     *
     * @param expenseDTO the entity to save.
     * @return the persisted entity.
     */
    ExpenseDTO save(ExpenseDTO expenseDTO);

    /**
     * Get all the expenses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ExpenseDTO> findAll(Pageable pageable);

    /**
     * Get the "id" expense.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ExpenseDTO> findOne(Long id);

    /**
     * Delete the "id" expense.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
    
    /**
     * Actualiza el coste de todos los gastos del evento a la nueva divisa dada.
     * 
     * @param eventDTO
     * @param oldEventCurrency
     */
	void updateCostToNewEventCurrencyType(EventDTO eventDTO, CurrencyType oldEventCurrency);
}
