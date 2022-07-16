package com.mariovn.eventmanager.service.impl;

import com.mariovn.eventmanager.domain.enumeration.CurrencyType;

public class CurrencyConverterException extends Exception {

	public CurrencyConverterException(CurrencyType currencyType) {
		super("Invalid CurrencyType " + currencyType);
	}
}
