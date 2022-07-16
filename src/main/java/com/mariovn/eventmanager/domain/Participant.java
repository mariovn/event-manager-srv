package com.mariovn.eventmanager.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

import com.mariovn.eventmanager.domain.enumeration.ParticipantType;

/**
 * Entidad con información extra del usuario para permitir relaciones
 */
@Entity
@Table(name = "participant")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Participant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ParticipantType type;

    @OneToMany(mappedBy = "participant")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Expense> expenses = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("participants")
    private Event event;

    @ManyToOne
    @JsonIgnoreProperties("participants")
    private UserExtra userExtra;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParticipantType getType() {
        return type;
    }

    public Participant type(ParticipantType type) {
        this.type = type;
        return this;
    }

    public void setType(ParticipantType type) {
        this.type = type;
    }

    public Set<Expense> getExpenses() {
        return expenses;
    }

    public Participant expenses(Set<Expense> expenses) {
        this.expenses = expenses;
        return this;
    }

    public Participant addExpenses(Expense expense) {
        this.expenses.add(expense);
        expense.setParticipant(this);
        return this;
    }

    public Participant removeExpenses(Expense expense) {
        this.expenses.remove(expense);
        expense.setParticipant(null);
        return this;
    }

    public void setExpenses(Set<Expense> expenses) {
        this.expenses = expenses;
    }

    public Event getEvent() {
        return event;
    }

    public Participant event(Event event) {
        this.event = event;
        return this;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public UserExtra getUserExtra() {
        return userExtra;
    }

    public Participant userExtra(UserExtra userExtra) {
        this.userExtra = userExtra;
        return this;
    }

    public void setUserExtra(UserExtra userExtra) {
        this.userExtra = userExtra;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Participant)) {
            return false;
        }
        return id != null && id.equals(((Participant) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Participant{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            "}";
    }
}
