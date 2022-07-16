package com.mariovn.eventmanager.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.mariovn.eventmanager.domain.enumeration.EventState;
import com.mariovn.eventmanager.domain.enumeration.CurrencyType;

/**
 * A DTO for the {@link com.mariovn.eventmanager.domain.Event} entity.
 */
@ApiModel(description = "Entidad Evento.")
public class EventDTO implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -5885232737349037875L;

	private Long id;

    /**
     * name
     */
    @NotNull
    @ApiModelProperty(value = "name", required = true)
    private String name;

    /**
     * description
     */
    @ApiModelProperty(value = "description")
    private String description;

    /**
     * Estado del evento
     */
    @ApiModelProperty(value = "Estado del evento")
    private EventState state;

    private Set<ExpenseDTO> expenses;
    
    private Set<ParticipantDTO> participants = new HashSet<>();

    /**
     * Tipo de cambio por defecto del evento
     */
    @NotNull
    @ApiModelProperty(value = "Tipo de cambio por defecto del evento", required = true)
    private CurrencyType currency;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventState getState() {
        return state;
    }

    public void setState(EventState state) {
        this.state = state;
    }

	public Set<ExpenseDTO> getExpenses() {
		return expenses;
	}

	public void setExpenses(Set<ExpenseDTO> expenses) {
		this.expenses = expenses;
	}

	public Set<ParticipantDTO> getParticipants() {
		return participants;
	}

    public CurrencyType getCurrency() {
        return currency;
    }

	public void setParticipantDTO(Set<ParticipantDTO> participants) {
		this.participants = participants;
	}
    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventDTO eventDTO = (EventDTO) o;
        if (eventDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), eventDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EventDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", state='" + getState() + "'" +
            ", currency='" + getCurrency() + "'" +
            "}";
    }
}
