package com.mariovn.eventmanager.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import com.mariovn.eventmanager.domain.enumeration.CurrencyType;

/**
 * A DTO for the {@link com.mariovn.eventmanager.domain.Currency} entity.
 */
@ApiModel(description = "Entidad moneda.")
public class CurrencyDTO implements Serializable {
    
    private Long id;

    /**
     * money
     */
    @NotNull
    @ApiModelProperty(value = "money", required = true)
    private CurrencyType currency;

    /**
     * value
     */
    @NotNull
    @DecimalMin(value = "0")
    @ApiModelProperty(value = "value", required = true)
    private Float value;

    /**
     * date of lass updated
     */
    @NotNull
    @ApiModelProperty(value = "date of lass updated", required = true)
    private Instant lastUpdated;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
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

        CurrencyDTO currencyDTO = (CurrencyDTO) o;
        if (currencyDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), currencyDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CurrencyDTO{" +
            "id=" + getId() +
            ", currency='" + getCurrency() + "'" +
            ", value=" + getValue() +
            ", lastUpdated='" + getLastUpdated() + "'" +
            "}";
    }
}
