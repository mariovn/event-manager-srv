package com.mariovn.eventmanager.service.impl;

import com.mariovn.eventmanager.service.ExpenseQueryService;
import com.mariovn.eventmanager.service.ExpenseService;
import com.mariovn.eventmanager.service.ParticipantQueryService;
import com.mariovn.eventmanager.service.UserService;
import com.mariovn.eventmanager.domain.Event;
import com.mariovn.eventmanager.domain.Expense;
import com.mariovn.eventmanager.domain.Participant;
import com.mariovn.eventmanager.domain.User;
import com.mariovn.eventmanager.domain.enumeration.CurrencyType;
import com.mariovn.eventmanager.domain.enumeration.ExpenseState;
import com.mariovn.eventmanager.repository.EventRepository;
import com.mariovn.eventmanager.repository.ExpenseRepository;
import com.mariovn.eventmanager.repository.ParticipantRepository;
import com.mariovn.eventmanager.service.dto.EventDTO;
import com.mariovn.eventmanager.service.dto.ExpenseCriteria;
import com.mariovn.eventmanager.service.dto.ExpenseCriteria.CurrencyTypeFilter;
import com.mariovn.eventmanager.service.dto.ExpenseDTO;
import com.mariovn.eventmanager.service.mapper.ExpenseMapper;

import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.InstantFilter;
import io.github.jhipster.service.filter.LongFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Expense}.
 */
@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final Logger log = LoggerFactory.getLogger(ExpenseServiceImpl.class);

    private final ExpenseRepository expenseRepository;

    @Autowired
    private UserService userService;
    
    @Autowired
    private ParticipantQueryService participantQueryService;
    
    @Autowired
    private CurrencyConverter currencyConverter;
    
    @Autowired
    private ExpenseQueryService expenseQueryService;
    
    @Autowired
    private ParticipantRepository participantRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    private final ExpenseMapper expenseMapper;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    /**
     * Save a expense.
     *
     * @param expenseDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ExpenseDTO save(ExpenseDTO expenseDTO) {
        log.debug("Request to save Expense : {}", expenseDTO);
        Expense expense = expenseMapper.toEntity(expenseDTO);

        User user = userService.getUserWithAuthorities().get();
        Long eventId = expenseDTO.getEventId();
        
        Event event = eventRepository.findById(eventId).get();
        
        if (event.getCurrency().equals(expenseDTO.getCurrencyType())) {
        	expense.setCost(expenseDTO.getCost());
        } else {
        	
        	try {
				Float converted = currencyConverter.convertToOtherCurrencyType(expenseDTO.getOriginalCost(), expenseDTO.getCurrencyType(), event.getCurrency());
				expense.setCost(converted);
			} catch (CurrencyConverterException e) {
				log.debug("Impossible to convert " + expenseDTO.getCurrencyType() + " to " + event.getCurrency());
			}
        }

        if (isDuplicated(expense)) {
			expense.setState(ExpenseState.DUPLICATED);
		}
        
        if (expense.getParticipant() == null) { 

	        Participant participant = participantQueryService.findByUserAndEvent(user, eventId);
			expense.setParticipant(participant);
			
			// Se actualiza el participante
	        participant.addExpenses(expense);
	        participantRepository.saveAndFlush(participant);
        }
		
		// Se almacena el gasto
        expense = expenseRepository.saveAndFlush(expense);
                
        // Se actualiza el evento
		event.addExpenses(expense);
		
        eventRepository.saveAndFlush(event);
        
        return expenseMapper.toDto(expense);
    }

    /**
     * Get all the expenses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Expenses");
        return expenseRepository.findAll(pageable)
            .map(expenseMapper::toDto);
    }

    /**
     * Get one expense by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ExpenseDTO> findOne(Long id) {
        log.debug("Request to get Expense : {}", id);
        return expenseRepository.findById(id)
            .map(expenseMapper::toDto);
    }

    /**
     * Delete the expense by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Expense : {}", id);
        Expense expense = expenseRepository.findById(id).get();
        
        // Se elimina el gasto del participante
        if (expense.getParticipant() != null) {
	        Participant participant = participantRepository.findById(expense.getParticipant().getId()).get();
	        participant.getExpenses().remove(expense);
	        participantRepository.saveAndFlush(participant);
        }
        
        // Se elimina el gasto del evento
        if (expense.getEvent() != null) {
	        Event event = eventRepository.findById(expense.getEvent().getId()).get();
	        event.getExpenses().remove(expense);
	        eventRepository.saveAndFlush(event);
        }
        
        expenseRepository.deleteById(id);
    }

    /**
     * Actualiza el coste de todos los gastos del evento a
     * 
     * @param eventDTO
     * @param oldEventCurrency
     */
	public void updateCostToNewEventCurrencyType(EventDTO eventDTO, CurrencyType oldEventCurrency) {
		if (eventDTO.getCurrency() != oldEventCurrency) {
    		ExpenseCriteria criteria = new ExpenseCriteria();
    		LongFilter longFilter = new LongFilter();
    		longFilter.setEquals(eventDTO.getId());
    		
    		criteria.setEventId(longFilter);
    		
			List<ExpenseDTO> expenses = expenseQueryService.findByCriteria(criteria);
			
			for (ExpenseDTO expenseDTO : expenses) {
				if (eventDTO.getCurrency().equals(expenseDTO.getCurrencyType())) {
					expenseDTO.setCost(expenseDTO.getOriginalCost());
				} else {
					try {
						Float newCurrency = currencyConverter.convertToOtherCurrencyType(expenseDTO.getOriginalCost(), expenseDTO.getCurrencyType(), eventDTO.getCurrency());
						expenseDTO.setCost(newCurrency);
					} catch (CurrencyConverterException e) {
						log.debug("Impossible to convert " + expenseDTO.getCurrencyType() + " to " + oldEventCurrency);
					}
				}
				expenseRepository.saveAndFlush(expenseMapper.toEntity((expenseDTO)));
			}
    	}
	}
	
	/**
	 * Método que comprueba si un gasto es un posible duplicado.
	 * 
	 * Para ser duplicado se tiene que dar que. El gasto sea un gasto creado nuevo y 
	 * exista uno o varios gastos del mismo evento creados el mismo día, con la misma moneda y el mismo coste.
	 * 
	 * @param expense gasto que se desea comprobar.
	 * 
	 * @return true si es un posible gasto duplicado.
	 */
	private boolean isDuplicated(final Expense expense) {
		boolean duplicated = false;
		
		boolean checkDuplicated = false;
		if (ExpenseState.NEW.equals(expense.getState())) {
			if (expense.getId() == null) {
				checkDuplicated = true;
			} else {
				Expense oldExpense = expenseRepository.findById(expense.getId()).get();
				if (ExpenseState.PENDING.equals(oldExpense.getState())) {
					checkDuplicated = true;
				}
			}
		}
		
		if (checkDuplicated) {
			
			ExpenseCriteria criteria = new ExpenseCriteria();
			
			LongFilter idFilter = new LongFilter();
			idFilter.setNotEquals(expense.getId());
			criteria.setId(idFilter );
			
			LongFilter eventFilter = new LongFilter();
			eventFilter.setEquals(expense.getEvent().getId());
			criteria.setEventId(eventFilter);
			
			FloatFilter originalCostFilter = new FloatFilter();
			originalCostFilter.setEquals(expense.getOriginalCost());
			criteria.setOriginalCost(originalCostFilter);
			
			CurrencyTypeFilter currencyTypeFilter = new CurrencyTypeFilter();
			currencyTypeFilter.equals(expense.getCurrencyType());
			criteria.setCurrencyType(currencyTypeFilter);
			
			long count = expenseQueryService.countByCriteria(criteria);
			
			duplicated = count > 0;
		}
		
		return duplicated;
	}
}

