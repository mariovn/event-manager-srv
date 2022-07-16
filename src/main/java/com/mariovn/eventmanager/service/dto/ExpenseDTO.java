package com.mariovn.eventmanager.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import com.mariovn.eventmanager.domain.enumeration.ExpenseState;
import com.mariovn.eventmanager.domain.enumeration.CurrencyType;

/**
 * A DTO for the {@link com.mariovn.eventmanager.domain.Expense} entity.
 */
@ApiModel(description = "Entidad gasto.")
public class ExpenseDTO implements Serializable {
    
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
     * Estado del gasto
     */
    @ApiModelProperty(value = "Estado del gasto")
    private ExpenseState state;

    /**
     * Coste en la moneda del evento
     */
    @NotNull
    @ApiModelProperty(value = "Coste en la moneda del evento", required = true)
    private Float cost;

    /**
     * Coste en la moneda del evento
     */
    @NotNull
    @ApiModelProperty(value = "Coste en la moneda del evento", required = true)
    private Float originalCost;

    /**
     * fecha del gasto
     */
    @NotNull
    @ApiModelProperty(value = "fecha del gasto", required = true)
    private Instant date;

    /**
     * moneda
     */
    @NotNull
    @ApiModelProperty(value = "moneda", required = true)
    private CurrencyType currencyType;

    /**
     * ticket o justificante
     */
    @ApiModelProperty(value = "ticket o justificante")
    @Lob
    private byte[] ticket;

    private String ticketContentType;

    private Long eventId;

    private Long participantId;
    
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

    public ExpenseState getState() {
        return state;
    }

    public void setState(ExpenseState state) {
        this.state = state;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public Float getOriginalCost() {
        return originalCost;
    }

    public void setOriginalCost(Float originalCost) {
        this.originalCost = originalCost;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    public byte[] getTicket() {
        return ticket;
    }

    public void setTicket(byte[] ticket) {
        this.ticket = ticket;
    }

    public String getTicketContentType() {
        return ticketContentType;
    }

    public void setTicketContentType(String ticketContentType) {
        this.ticketContentType = ticketContentType;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExpenseDTO expenseDTO = (ExpenseDTO) o;
        if (expenseDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), expenseDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ExpenseDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", state='" + getState() + "'" +
            ", cost=" + getCost() +
            ", originalCost=" + getOriginalCost() +
            ", date='" + getDate() + "'" +
            ", currencyType='" + getCurrencyType() + "'" +
            ", ticket='" + getTicket() + "'" +
            ", eventId=" + getEventId() +
            ", participantId=" + getParticipantId() +
            "}";
    }
}
