package com.mariovn.eventmanager.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import com.mariovn.eventmanager.domain.enumeration.ExpenseState;
import com.mariovn.eventmanager.domain.enumeration.CurrencyType;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.mariovn.eventmanager.domain.Expense} entity. This class is used
 * in {@link com.mariovn.eventmanager.web.rest.ExpenseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /expenses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ExpenseCriteria implements Serializable, Criteria {
    /**
     * Class for filtering ExpenseState
     */
    public static class ExpenseStateFilter extends Filter<ExpenseState> {

        public ExpenseStateFilter() {
        }

        public ExpenseStateFilter(ExpenseStateFilter filter) {
            super(filter);
        }

        @Override
        public ExpenseStateFilter copy() {
            return new ExpenseStateFilter(this);
        }

    }
    /**
     * Class for filtering CurrencyType
     */
    public static class CurrencyTypeFilter extends Filter<CurrencyType> {

        public CurrencyTypeFilter() {
        }

        public CurrencyTypeFilter(CurrencyTypeFilter filter) {
            super(filter);
        }

        @Override
        public CurrencyTypeFilter copy() {
            return new CurrencyTypeFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter description;

    private ExpenseStateFilter state;

    private FloatFilter cost;

    private FloatFilter originalCost;

    private InstantFilter date;

    private CurrencyTypeFilter currencyType;

    private LongFilter eventId;

    private LongFilter participantId;

    public ExpenseCriteria() {
    }

    public ExpenseCriteria(ExpenseCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.state = other.state == null ? null : other.state.copy();
        this.cost = other.cost == null ? null : other.cost.copy();
        this.originalCost = other.originalCost == null ? null : other.originalCost.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.currencyType = other.currencyType == null ? null : other.currencyType.copy();
        this.eventId = other.eventId == null ? null : other.eventId.copy();
        this.participantId = other.participantId == null ? null : other.participantId.copy();
    }

    @Override
    public ExpenseCriteria copy() {
        return new ExpenseCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public ExpenseStateFilter getState() {
        return state;
    }

    public void setState(ExpenseStateFilter state) {
        this.state = state;
    }

    public FloatFilter getCost() {
        return cost;
    }

    public void setCost(FloatFilter cost) {
        this.cost = cost;
    }

    public FloatFilter getOriginalCost() {
        return originalCost;
    }

    public void setOriginalCost(FloatFilter originalCost) {
        this.originalCost = originalCost;
    }

    public InstantFilter getDate() {
        return date;
    }

    public void setDate(InstantFilter date) {
        this.date = date;
    }

    public CurrencyTypeFilter getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(CurrencyTypeFilter currencyType) {
        this.currencyType = currencyType;
    }

    public LongFilter getEventId() {
        return eventId;
    }

    public void setEventId(LongFilter eventId) {
        this.eventId = eventId;
    }

    public LongFilter getParticipantId() {
        return participantId;
    }

    public void setParticipantId(LongFilter participantId) {
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
        final ExpenseCriteria that = (ExpenseCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(state, that.state) &&
            Objects.equals(cost, that.cost) &&
            Objects.equals(originalCost, that.originalCost) &&
            Objects.equals(date, that.date) &&
            Objects.equals(currencyType, that.currencyType) &&
            Objects.equals(eventId, that.eventId) &&
            Objects.equals(participantId, that.participantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        description,
        state,
        cost,
        originalCost,
        date,
        currencyType,
        eventId,
        participantId
        );
    }

    @Override
    public String toString() {
        return "ExpenseCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (state != null ? "state=" + state + ", " : "") +
                (cost != null ? "cost=" + cost + ", " : "") +
                (originalCost != null ? "originalCost=" + originalCost + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (currencyType != null ? "currencyType=" + currencyType + ", " : "") +
                (eventId != null ? "eventId=" + eventId + ", " : "") +
                (participantId != null ? "participantId=" + participantId + ", " : "") +
            "}";
    }

}
