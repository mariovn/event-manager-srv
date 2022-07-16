package com.mariovn.eventmanager.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;
import java.time.Instant;

import com.mariovn.eventmanager.domain.enumeration.ExpenseState;

import com.mariovn.eventmanager.domain.enumeration.CurrencyType;

/**
 * Entidad gasto.
 */
@Entity
@Table(name = "expense")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Expense implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * name
     */
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * description
     */
    @Column(name = "description")
    private String description;

    /**
     * Estado del gasto
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private ExpenseState state;

    /**
     * Coste en la moneda del evento
     */
    @NotNull
    @Column(name = "cost", nullable = false)
    private Float cost;

    /**
     * Coste en la moneda del evento
     */
    @NotNull
    @Column(name = "original_cost", nullable = false)
    private Float originalCost;

    /**
     * fecha del gasto
     */
    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    /**
     * moneda
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "currency_type", nullable = false)
    private CurrencyType currencyType;

    /**
     * ticket o justificante
     */
    @Lob
    @Column(name = "ticket")
    private byte[] ticket;

    @Column(name = "ticket_content_type")
    private String ticketContentType;

    @ManyToOne
    @JsonIgnoreProperties("expenses")
    private Event event;

    @ManyToOne
    @JsonIgnoreProperties("expenses")
    private Participant participant;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Expense name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Expense description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExpenseState getState() {
        return state;
    }

    public Expense state(ExpenseState state) {
        this.state = state;
        return this;
    }

    public void setState(ExpenseState state) {
        this.state = state;
    }

    public Float getCost() {
        return cost;
    }

    public Expense cost(Float cost) {
        this.cost = cost;
        return this;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public Float getOriginalCost() {
        return originalCost;
    }

    public Expense originalCost(Float originalCost) {
        this.originalCost = originalCost;
        return this;
    }

    public void setOriginalCost(Float originalCost) {
        this.originalCost = originalCost;
    }

    public Instant getDate() {
        return date;
    }

    public Expense date(Instant date) {
        this.date = date;
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public Expense currencyType(CurrencyType currencyType) {
        this.currencyType = currencyType;
        return this;
    }

    public void setCurrencyType(CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    public byte[] getTicket() {
        return ticket;
    }

    public Expense ticket(byte[] ticket) {
        this.ticket = ticket;
        return this;
    }

    public void setTicket(byte[] ticket) {
        this.ticket = ticket;
    }

    public String getTicketContentType() {
        return ticketContentType;
    }

    public Expense ticketContentType(String ticketContentType) {
        this.ticketContentType = ticketContentType;
        return this;
    }

    public void setTicketContentType(String ticketContentType) {
        this.ticketContentType = ticketContentType;
    }

    public Event getEvent() {
        return event;
    }

    public Expense event(Event event) {
        this.event = event;
        return this;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Participant getParticipant() {
        return participant;
    }

    public Expense participant(Participant participant) {
        this.participant = participant;
        return this;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Expense)) {
            return false;
        }
        return id != null && id.equals(((Expense) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Expense{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", state='" + getState() + "'" +
            ", cost=" + getCost() +
            ", originalCost=" + getOriginalCost() +
            ", date='" + getDate() + "'" +
            ", currencyType='" + getCurrencyType() + "'" +
            ", ticket='" + getTicket() + "'" +
            ", ticketContentType='" + getTicketContentType() + "'" +
            "}";
    }
}
