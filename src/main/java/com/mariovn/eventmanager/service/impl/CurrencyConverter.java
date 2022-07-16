package com.mariovn.eventmanager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.mariovn.eventmanager.domain.Currency;
import com.mariovn.eventmanager.domain.enumeration.CurrencyType;
import com.mariovn.eventmanager.repository.CurrencyRepository;

@Service
public class CurrencyConverter {
	
	@Autowired
	private CurrencyRepository currencyRepository;

	/**
	 * Convierte el valor dado de un tipo a otro nuevo.
	 * 
	 * @param value valor que se desea convertir.
	 * @param oldCurrencyType moneda actual.
	 * @param newCurrencyType moneda a la que se desea convertir.
	 * 
	 * @return valor en la nueva moneda.
	 * 
	 * @throws CurrencyConverterException si no se encuentra alguno de los tipo de moneda dados.
	 */
	public Float convertToOtherCurrencyType(final float value, final CurrencyType oldCurrencyType, final CurrencyType newCurrencyType) throws CurrencyConverterException {
		
		if (oldCurrencyType.equals(newCurrencyType)) {
			return value;
		}
		
		float valueInEuro = convertToEuro(value, oldCurrencyType);
		
		return convertFromEuro(valueInEuro, newCurrencyType);
	}

	/**
	 * Convierte a euros desde otra moneda
	 * @param value valor a convertir
	 * @param currencyType nuevo tipo
	 * 
	 * @return valor convertido a euros
	 * 
	 * @throws CurrencyConverterException si no se encuentra el tipo de moneda dado.
	 */
	private float convertToEuro(final float value, final CurrencyType currencyType) throws CurrencyConverterException {
		
		Currency currency = currencyRepository.findOneCurrencyByCurrency(currencyType);
		
		if (currency == null) {
			throw new CurrencyConverterException(currencyType);
		}
		
		return value / currency.getValue();
	}
	
	/**
	 * Convierte desde euros a otra moneda
	 * @param value
	 * @param currencyType
	 * 
	 * @return valor en la nueva moneda
	 * 
	 * @throws CurrencyConverterException si no se encuentra el tipo de moneda dado.
	 */
	private float convertFromEuro(final float value, final CurrencyType currencyType) throws CurrencyConverterException {
		Currency currency = currencyRepository.findOneCurrencyByCurrency(currencyType);
		
		if (currency == null) {
			throw new CurrencyConverterException(currencyType);
		}
		
		float convertedValue = value * currency.getValue();
		
		return (float) (Math.round(convertedValue * 100.0) / 100.0);
	}
}
