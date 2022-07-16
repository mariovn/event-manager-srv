package com.mariovn.eventmanager.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
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
 * Criteria class for the {@link com.mariovn.eventmanager.domain.Currency} entity. This class is used
 * in {@link com.mariovn.eventmanager.web.rest.CurrencyResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /currencies?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CurrencyCriteria implements Serializable, Criteria {
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

    private CurrencyTypeFilter currency;

    private FloatFilter value;

    private InstantFilter lastUpdated;

    public CurrencyCriteria() {
    }

    public CurrencyCriteria(CurrencyCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.currency = other.currency == null ? null : other.currency.copy();
        this.value = other.value == null ? null : other.value.copy();
        this.lastUpdated = other.lastUpdated == null ? null : other.lastUpdated.copy();
    }

    @Override
    public CurrencyCriteria copy() {
        return new CurrencyCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public CurrencyTypeFilter getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyTypeFilter currency) {
        this.currency = currency;
    }

    public FloatFilter getValue() {
        return value;
    }

    public void setValue(FloatFilter value) {
        this.value = value;
    }

    public InstantFilter getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(InstantFilter lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CurrencyCriteria that = (CurrencyCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(currency, that.currency) &&
            Objects.equals(value, that.value) &&
            Objects.equals(lastUpdated, that.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        currency,
        value,
        lastUpdated
        );
    }

    @Override
    public String toString() {
        return "CurrencyCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (currency != null ? "currency=" + currency + ", " : "") +
                (value != null ? "value=" + value + ", " : "") +
                (lastUpdated != null ? "lastUpdated=" + lastUpdated + ", " : "") +
            "}";
    }

}
