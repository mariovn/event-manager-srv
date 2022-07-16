package com.mariovn.eventmanager.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;
import java.time.Instant;

import com.mariovn.eventmanager.domain.enumeration.CurrencyType;

/**
 * Entidad moneda.
 */
@Entity
@Table(name = "currency")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Currency implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * money
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, unique = true)
    private CurrencyType currency;

    /**
     * value
     */
    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "value", nullable = false)
    private Float value;

    /**
     * date of lass updated
     */
    @NotNull
    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public Currency currency(CurrencyType currency) {
        this.currency = currency;
        return this;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    public Float getValue() {
        return value;
    }

    public Currency value(Float value) {
        this.value = value;
        return this;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public Currency lastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Currency)) {
            return false;
        }
        return id != null && id.equals(((Currency) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Currency{" +
            "id=" + getId() +
            ", currency='" + getCurrency() + "'" +
            ", value=" + getValue() +
            ", lastUpdated='" + getLastUpdated() + "'" +
            "}";
    }
}
