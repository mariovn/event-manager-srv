package com.mariovn.eventmanager.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import com.mariovn.eventmanager.domain.enumeration.ParticipantType;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.mariovn.eventmanager.domain.Participant} entity. This class is used
 * in {@link com.mariovn.eventmanager.web.rest.ParticipantResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /participants?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ParticipantCriteria implements Serializable, Criteria {
    /**
     * Class for filtering ParticipantType
     */
    public static class ParticipantTypeFilter extends Filter<ParticipantType> {

        public ParticipantTypeFilter() {
        }

        public ParticipantTypeFilter(ParticipantTypeFilter filter) {
            super(filter);
        }

        @Override
        public ParticipantTypeFilter copy() {
            return new ParticipantTypeFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ParticipantTypeFilter type;

    private LongFilter expensesId;

    private LongFilter eventId;

    private LongFilter userExtraId;

    public ParticipantCriteria() {
    }

    public ParticipantCriteria(ParticipantCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.expensesId = other.expensesId == null ? null : other.expensesId.copy();
        this.eventId = other.eventId == null ? null : other.eventId.copy();
        this.userExtraId = other.userExtraId == null ? null : other.userExtraId.copy();
    }

    @Override
    public ParticipantCriteria copy() {
        return new ParticipantCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public ParticipantTypeFilter getType() {
        return type;
    }

    public void setType(ParticipantTypeFilter type) {
        this.type = type;
    }

    public LongFilter getExpensesId() {
        return expensesId;
    }

    public void setExpensesId(LongFilter expensesId) {
        this.expensesId = expensesId;
    }

    public LongFilter getEventId() {
        return eventId;
    }

    public void setEventId(LongFilter eventId) {
        this.eventId = eventId;
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
        final ParticipantCriteria that = (ParticipantCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(expensesId, that.expensesId) &&
            Objects.equals(eventId, that.eventId) &&
            Objects.equals(userExtraId, that.userExtraId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        type,
        expensesId,
        eventId,
        userExtraId
        );
    }

    @Override
    public String toString() {
        return "ParticipantCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (type != null ? "type=" + type + ", " : "") +
                (expensesId != null ? "expensesId=" + expensesId + ", " : "") +
                (eventId != null ? "eventId=" + eventId + ", " : "") +
                (userExtraId != null ? "userExtraId=" + userExtraId + ", " : "") +
            "}";
    }

}
