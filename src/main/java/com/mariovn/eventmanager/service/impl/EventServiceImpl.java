package com.mariovn.eventmanager.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.mariovn.eventmanager.domain.Event;
import com.mariovn.eventmanager.domain.Expense;
import com.mariovn.eventmanager.domain.Participant;
import com.mariovn.eventmanager.domain.User;
import com.mariovn.eventmanager.domain.UserExtra;
import com.mariovn.eventmanager.domain.enumeration.EventState;
import com.mariovn.eventmanager.domain.enumeration.ExpenseState;
import com.mariovn.eventmanager.domain.enumeration.ParticipantType;
import com.mariovn.eventmanager.repository.EventRepository;
import com.mariovn.eventmanager.repository.ExpenseRepository;
import com.mariovn.eventmanager.repository.ParticipantRepository;
import com.mariovn.eventmanager.repository.UserExtraRepository;
import com.mariovn.eventmanager.service.EventService;
import com.mariovn.eventmanager.service.ExpenseService;
import com.mariovn.eventmanager.service.UserService;
import com.mariovn.eventmanager.service.dto.EventDTO;
import com.mariovn.eventmanager.service.mapper.EventMapper;

/**
 * Service Implementation for managing {@link Event}.
 */
@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;
    
    @Autowired
    private UserService userService;
    	
    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private EventResumeService eventResumeService;

    @Autowired
	private UserExtraRepository userExtraRepository;
	
	@Autowired
	private ParticipantRepository participantRepository;
	
	@Autowired
	private ExpenseRepository expenseRespository;
	
	
    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    /**
     * Save a event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public EventDTO save(EventDTO eventDTO) {
    	
    	Optional<Event> optional = null;
    	
    	if (eventDTO.getId() != null) {
    	 optional = eventRepository.findById(eventDTO.getId());
    	}
    	
    	if (optional == null || !optional.isPresent()) {
    		eventDTO.setState(EventState.NEW);
    		Event updatedEvent = eventRepository.saveAndFlush(eventMapper.toEntity(eventDTO));
			
			UserExtra userExtra = getUserInfo();
			
			Participant participant = new Participant();
			participant.setEvent(updatedEvent);
			participant.setUserExtra(userExtra);
			participant.setType(ParticipantType.OWNER);
			
			updatedEvent.addParticipants(participant);
			userExtra.addParticipant(participant);
			
			participantRepository.save(participant);
			
			userExtraRepository.save(userExtra);
			
    	
			return eventMapper.toDto(eventRepository.save(updatedEvent));
    	}
    	
    	Event oldEvent = optional.get();

    	expenseService.updateCostToNewEventCurrencyType(eventDTO, oldEvent.getCurrency());
    	
    	return eventMapper.toDto(eventRepository.save(eventMapper.toEntity(eventDTO)));
    }

    /**
     * Get all the events.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Events");
        return eventRepository.findAll(pageable)
            .map(eventMapper::toDto);
    }

    /**
     * Get one event by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EventDTO> findOne(Long id) {
        log.debug("Request to get Event : {}", id);
        return eventRepository.findById(id)
            .map(eventMapper::toDto);
    }

    /**
     * Delete the event by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Event : {}", id);
        
        Optional<Event> event = eventRepository.findById(id);
        
        Set<Participant> participants = event.get().getParticipants();
        Set<Expense> expenses = event.get().getExpenses();
        
        if (!CollectionUtils.isEmpty(expenses)) {
        	expenseRespository.deleteAll(expenses);
        }
        if (!CollectionUtils.isEmpty(participants)) {
        	participantRepository.deleteAll(participants);
        }
        
        eventRepository.deleteById(id);
    }

	@Override
	public InputStreamResource userEventInform(Long id) {
		UserExtra userExtra = getUserInfo();
		
		Optional<Event> eventOptional = eventRepository.findById(id);
		
		if (eventOptional.isPresent()) {

			Event event = eventOptional.get();
						
			Set<Expense> userExpenses = findUserExpenses(userExtra, event);

			try {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				
				Document document = new Document();
				
				PdfWriter.getInstance(document, output);
				
				document.addTitle(event.getName() + "_resume");
								
				document.open();
				
				eventResumeService.fillDocumentContent(document, event, userExpenses, this.getUserInfo().getUser());

				document.close();
				
				ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
				
				return new InputStreamResource(input);
				
			} catch (DocumentException e) {
				log.error("No se ha podido generar el pdf");
			}
		}
		
		return null;
	}

	/**
	 * Método que obtiene los gastos del usuario en el evento.
	 * 
	 * @param userExtra
	 * @param event
	 * @return
	 */
	private Set<Expense> findUserExpenses(UserExtra userExtra, Event event) {
		Set<Expense> userExpenses = new HashSet<Expense>();
		
		Set<Expense> expenses = event.getExpenses();
		for (Expense expense : expenses) {
			if (userExtra.equals(expense.getParticipant().getUserExtra()) 
					&& ExpenseState.NEW.equals(expense.getState())) {
				userExpenses.add(expense);
			}
		}
		
		return userExpenses;
	}
    
	/**
	 * Método que obtiene la información del usuario logeado.
	 * @return información del usuario.
	 */
	private UserExtra getUserInfo() {
		User user = userService.getUserWithAuthorities().get();
		    
		UserExtra userExtra = userExtraRepository.findById(user.getId()).get();
		return userExtra;
	}
    
}
