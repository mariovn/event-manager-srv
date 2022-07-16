package com.mariovn.eventmanager.service.dto;

import io.swagger.annotations.ApiModel;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import com.mariovn.eventmanager.domain.enumeration.ParticipantType;

/**
 * A DTO for the {@link com.mariovn.eventmanager.domain.Participant} entity.
 */
@ApiModel(description = "Entidad con información extra del usuario para permitir relaciones")
public class ParticipantDTO implements Serializable {
    
    private Long id;

    @NotNull
    private ParticipantType type;

    private Long eventId;

    private Long userExtraId;

    private String name;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParticipantType getType() {
        return type;
    }

    public void setType(ParticipantType type) {
        this.type = type;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getUserExtraId() {
        return userExtraId;
    }

    public void setUserExtraId(Long userExtraId) {
        this.userExtraId = userExtraId;
    }
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ParticipantDTO participantDTO = (ParticipantDTO) o;
        if (participantDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), participantDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ParticipantDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", eventId=" + getEventId() +
            ", userExtraId=" + getUserExtraId() +
            ", name=" + getName() +
            "}";
    }

}
