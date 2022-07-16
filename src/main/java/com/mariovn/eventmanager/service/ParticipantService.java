package com.mariovn.eventmanager.service;

import com.mariovn.eventmanager.domain.Participant;
import com.mariovn.eventmanager.service.dto.ParticipantDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

/**
 * Service Interface for managing {@link com.mariovn.eventmanager.domain.Participant}.
 */
public interface ParticipantService {

    /**
     * Save a participant.
     *
     * @param participantDTO the entity to save.
     * @return the persisted entity.
     */
    ParticipantDTO save(ParticipantDTO participantDTO);
    
    /**
     * Save a participant.
     *
     * @param participant to save.
     * @return the persisted entity.
     */
    ParticipantDTO save(Participant participant);

    /**
     * Get all the participants.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ParticipantDTO> findAll(Pageable pageable);

    /**
     * Get the "id" participant.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ParticipantDTO> findOne(Long id);

    /**
     * Delete the "id" participant.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

}
