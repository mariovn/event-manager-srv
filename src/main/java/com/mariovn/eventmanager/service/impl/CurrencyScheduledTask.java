package com.mariovn.eventmanager.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mariovn.eventmanager.domain.Currency;
import com.mariovn.eventmanager.domain.enumeration.CurrencyType;
import com.mariovn.eventmanager.repository.CurrencyRepository;

/**
 * Clase para obtener el valor del cambio de divisass
 *
 */
@Service
public class CurrencyScheduledTask {
		
	private static final String URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
	private static final String CUBE = "//Cube/Cube/Cube";
	private static final String CURRENCY = "currency";
	private static final String RATE = "rate";

	@Autowired
	private CurrencyRepository currencyRepository;

	/*
	 * Método que se ejecuta cada hora y actualiza los valores del cambio de divisa.
	 */
	@Scheduled(fixedRate = 3600000)
	public void updateCurrencyTask() {
		
		Map<CurrencyType, Float> currencyValues = currencyParser();
		
		currencyValues.put(CurrencyType.EUR, new Float(1));
		
		if (!CollectionUtils.isEmpty(currencyValues)) {
			for (CurrencyType currencyType : CurrencyType.values()) {
				Currency currency = currencyRepository.findOneCurrencyByCurrency(currencyType);
				
				if (currency == null) {
					currency = new Currency();
					currency.setCurrency(currencyType);
				}
				
				Float value = currencyValues.get(currency.getCurrency());
				if (value != null) {
					currency.setValue(value);
					currency.setLastUpdated(Instant.now());
					
					currencyRepository.saveAndFlush(currency);
				}
			}
		}
	}
	
	/**
	 * Método que obtiene el valor del cambio de divisa a partir del euro.
	 * @return
	 */
	private Map<CurrencyType, Float> currencyParser() {
		Map<CurrencyType, Float> map = new HashMap<>();
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder builder = null;
		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO mariovn: añadir log
			e.printStackTrace();
		}
		
		try {
			URL url = new URL(URL);

			InputStream is = url.openStream();
			
			Document document = builder.parse(is);
			
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xPath = xPathFactory.newXPath();
			XPathExpression expression = xPath.compile(CUBE);
			
			NodeList nodeList = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				
				NamedNodeMap attributes = node.getAttributes();
				if (attributes.getLength() > 0) {
					String currency = attributes.getNamedItem(CURRENCY).getNodeValue();
					String rate = attributes.getNamedItem(RATE).getNodeValue();
										
					try {
						map.put(Enum.valueOf(CurrencyType.class, currency), Float.parseFloat(rate));
					
					} catch (IllegalArgumentException e) {
						// TODO mariovn: añadir log
						e.printStackTrace();
					}
				}
			}
			
			
		} catch (IOException | SAXException | XPathExpressionException e) {
			// TODO mariovn: añadir log
			e.printStackTrace();
		}
		
		return map;
	}
}
