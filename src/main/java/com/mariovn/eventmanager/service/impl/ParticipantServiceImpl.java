package com.mariovn.eventmanager.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mariovn.eventmanager.domain.Event;
import com.mariovn.eventmanager.domain.Participant;
import com.mariovn.eventmanager.domain.User;
import com.mariovn.eventmanager.domain.UserExtra;
import com.mariovn.eventmanager.repository.EventRepository;
import com.mariovn.eventmanager.repository.ParticipantRepository;
import com.mariovn.eventmanager.repository.UserExtraRepository;
import com.mariovn.eventmanager.service.ParticipantService;
import com.mariovn.eventmanager.service.UserService;
import com.mariovn.eventmanager.service.dto.ParticipantDTO;
import com.mariovn.eventmanager.service.mapper.ParticipantMapper;

/**
 * Service Implementation for managing {@link Participant}.
 */
@Service
@Transactional
public class ParticipantServiceImpl implements ParticipantService {

    private final Logger log = LoggerFactory.getLogger(ParticipantServiceImpl.class);

    private final ParticipantRepository participantRepository;

    @Autowired
    private EventRepository eventRepository;
    
    private final ParticipantMapper participantMapper;

    public ParticipantServiceImpl(ParticipantRepository participantRepository, ParticipantMapper participantMapper) {
		this.participantRepository = participantRepository;
        this.participantMapper = participantMapper;
    }

    /**
     * Save a participant.
     *
     * @param participantDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ParticipantDTO save(ParticipantDTO participantDTO) {
        log.debug("Request to save Participant : {}", participantDTO);
        Participant participant = participantMapper.toEntity(participantDTO);
        participant = participantRepository.save(participant);
        
        addParticipantInEvent(participant);
        
        return participantMapper.toDto(participant);
    }
    
    /**
     * Save a participant.
     *
     * @param participant to save.
     * @return the persisted entity.
     */
    @Override
    public ParticipantDTO save(Participant participant) {
        log.debug("Request to save Participant : {}", participant);
        
        Participant updatedParticipant = participantRepository.save(participant);
        addParticipantInEvent(updatedParticipant);
        
        return participantMapper.toDto(updatedParticipant);
    }

    /**
     * Get all the participants.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ParticipantDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Participants");
        return participantRepository.findAll(pageable)
            .map(participantMapper::toDto);
    }

    /**
     * Get one participant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ParticipantDTO> findOne(Long id) {
        log.debug("Request to get Participant : {}", id);
        
        return participantRepository.findById(id)
            .map(participantMapper::toDto);
    }
    
    /**
     * Delete the participant by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Participant : {}", id);
        
        Participant participant = participantRepository.findById(id).get();
        
        if (participant.getEvent() != null) {
        	Event event = eventRepository.findById(participant.getEvent().getId()).get();
        	event.removeParticipants(participant);
        	eventRepository.save(event);
        }
        
        participantRepository.deleteById(id);
    }
    
    /**
     * Método que añade el participante el evento
     * @param participant
     */
	private void addParticipantInEvent(Participant participant) {
		Event event = eventRepository.findById(participant.getEvent().getId()).get();
        event.addParticipants(participant);
        eventRepository.saveAndFlush(event);
	}
    
}

