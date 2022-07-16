package com.mariovn.eventmanager.service.mapper;


import com.mariovn.eventmanager.domain.*;
import com.mariovn.eventmanager.service.dto.ParticipantDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Participant} and its DTO {@link ParticipantDTO}.
 */
@Mapper(componentModel = "spring", uses = {EventMapper.class, UserExtraMapper.class})
public interface ParticipantMapper extends EntityMapper<ParticipantDTO, Participant> {

    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "userExtra.id", target = "userExtraId")
    @Mapping(source = "userExtra.user.login", target = "name")
    ParticipantDTO toDto(Participant participant);

    @Mapping(target = "expenses", ignore = true)
    @Mapping(target = "removeExpenses", ignore = true)
    @Mapping(source = "eventId", target = "event")
    @Mapping(source = "userExtraId", target = "userExtra")
    Participant toEntity(ParticipantDTO participantDTO);

    default Participant fromId(Long id) {
        if (id == null) {
            return null;
        }
        Participant participant = new Participant();
        participant.setId(id);
        return participant;
    }
}
