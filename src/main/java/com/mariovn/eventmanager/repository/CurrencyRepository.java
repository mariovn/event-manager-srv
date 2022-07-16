package com.mariovn.eventmanager.repository;

import com.mariovn.eventmanager.domain.Currency;
import com.mariovn.eventmanager.domain.enumeration.CurrencyType;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Currency entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long>, JpaSpecificationExecutor<Currency> {
	
	Currency findOneCurrencyByCurrency(CurrencyType currency);
	
}
