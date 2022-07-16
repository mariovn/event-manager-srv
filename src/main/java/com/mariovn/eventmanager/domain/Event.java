package com.mariovn.eventmanager.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.mariovn.eventmanager.domain.enumeration.CurrencyType;
import com.mariovn.eventmanager.domain.enumeration.EventState;

/**
 * Entidad Evento.
 */
@Entity
@Table(name = "event")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Event implements Serializable {

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
     * Estado del evento
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private EventState state;

    /**
     * Tipo de cambio por defecto del evento
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyType currency;

    @OneToMany(mappedBy = "event")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Expense> expenses = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Participant> participants = new HashSet<>();

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

    public Event name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Event description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventState getState() {
        return state;
    }

    public Event state(EventState state) {
        this.state = state;
        return this;
    }

    public void setState(EventState state) {
        this.state = state;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public Event currency(CurrencyType currency) {
        this.currency = currency;
        return this;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    public Set<Expense> getExpenses() {
        return expenses;
    }

    public Event expenses(Set<Expense> expenses) {
        this.expenses = expenses;
        return this;
    }

    public Event addExpenses(Expense expense) {
        this.expenses.add(expense);
        expense.setEvent(this);
        return this;
    }

    public Event removeExpenses(Expense expense) {
        this.expenses.remove(expense);
        expense.setEvent(null);
        return this;
    }

    public void setExpenses(Set<Expense> expenses) {
        this.expenses = expenses;
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public Event participants(Set<Participant> participants) {
        this.participants = participants;
        return this;
    }

    public Event addParticipants(Participant participant) {
        this.participants.add(participant);
        participant.setEvent(this);
        return this;
    }

    public Event removeParticipants(Participant participant) {
        this.participants.remove(participant);
        participant.setEvent(null);
        return this;
    }

    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        return id != null && id.equals(((Event) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Event{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", state='" + getState() + "'" +
            ", currency='" + getCurrency() + "'" +
            "}";
    }
}
