package com.mariovn.eventmanager.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import com.mariovn.eventmanager.domain.enumeration.EventState;
import com.mariovn.eventmanager.domain.enumeration.CurrencyType;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.mariovn.eventmanager.domain.Event} entity. This class is used
 * in {@link com.mariovn.eventmanager.web.rest.EventResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /events?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class EventCriteria implements Serializable, Criteria {
    /**
     * Class for filtering EventState
     */
    public static class EventStateFilter extends Filter<EventState> {

        public EventStateFilter() {
        }

        public EventStateFilter(EventStateFilter filter) {
            super(filter);
        }

        @Override
        public EventStateFilter copy() {
            return new EventStateFilter(this);
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

    private EventStateFilter state;

    private CurrencyTypeFilter currency;

    private LongFilter expensesId;

    private LongFilter participantsId;
    
    private LongFilter userExtraId;

    public EventCriteria() {
    }

    public EventCriteria(EventCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.state = other.state == null ? null : other.state.copy();
        this.currency = other.currency == null ? null : other.currency.copy();
        this.expensesId = other.expensesId == null ? null : other.expensesId.copy();
        this.participantsId = other.participantsId == null ? null : other.participantsId.copy();
        this.userExtraId = other.userExtraId == null ? null : other.userExtraId.copy();
    }

    @Override
    public EventCriteria copy() {
        return new EventCriteria(this);
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

    public EventStateFilter getState() {
        return state;
    }

    public void setState(EventStateFilter state) {
        this.state = state;
    }

    public CurrencyTypeFilter getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyTypeFilter currency) {
        this.currency = currency;
    }

    public LongFilter getExpensesId() {
        return expensesId;
    }

    public void setExpensesId(LongFilter expensesId) {
        this.expensesId = expensesId;
    }

    public LongFilter getParticipantsId() {
        return participantsId;
    }

    public void setParticipantsId(LongFilter participantsId) {
        this.participantsId = participantsId;
    }

    public LongFilter getUserExtraId() {
		return userExtraId;
	}

	public void setUserExtraId(LongFilter userExtraId) {
		this.userExtraId = userExtraId;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EventCriteria that = (EventCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(state, that.state) &&
            Objects.equals(currency, that.currency) &&
            Objects.equals(expensesId, that.expensesId) &&
            Objects.equals(participantsId, that.participantsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        description,
        state,
        currency,
        expensesId,
        participantsId,
        userExtraId
        );
    }

    @Override
    public String toString() {
        return "EventCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (state != null ? "state=" + state + ", " : "") +
                (currency != null ? "currency=" + currency + ", " : "") +
                (expensesId != null ? "expensesId=" + expensesId + ", " : "") +
                (participantsId != null ? "participantsId=" + participantsId + ", " : "") +
            	(userExtraId != null ? "userExtraId=" + userExtraId + ", " : "") +
            	 
            "}";
    }

}
