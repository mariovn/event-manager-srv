package com.mariovn.eventmanager.service.mapper;


import com.mariovn.eventmanager.domain.*;
import com.mariovn.eventmanager.service.dto.EventDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Event} and its DTO {@link EventDTO}.
 */
@Mapper(componentModel = "spring", uses = {ExpenseSimpleMapper.class, ParticipantMapper.class})
public interface EventMapper extends EntityMapper<EventDTO, Event> {
	
	@Mapping(source = "event.expenses", target = "expenses")
	@Mapping(source = "event.participants", target = "participants")
	EventDTO toDto(Event event);
	
    @Mapping(target = "expenses")
    @Mapping(target = "removeExpenses", ignore = true)
    @Mapping(target = "participants")
    @Mapping(target = "removeParticipants", ignore = true)
    Event toEntity(EventDTO eventDTO);
    
    default Event fromId(Long id) {
        if (id == null) {
            return null;
        }
        Event event = new Event();
        event.setId(id);
        return event;
    }
}
