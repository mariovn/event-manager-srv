package com.mariovn.eventmanager.service;

import java.util.Optional;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mariovn.eventmanager.service.dto.EventDTO;

/**
 * Service Interface for managing {@link com.mariovn.eventmanager.domain.Event}.
 */
public interface EventService {

    /**
     * Save a event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    EventDTO save(EventDTO eventDTO);

    /**
     * Get all the events.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EventDTO> findAll(Pageable pageable);

    /**
     * Get the "id" event.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EventDTO> findOne(Long id);

    /**
     * Delete the "id" event.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Servicio que compone un informe del evento a partir del id dado.
     * 
     * @param id identificador del evento.
     * @return informe
     */
	InputStreamResource userEventInform(Long id);
}
