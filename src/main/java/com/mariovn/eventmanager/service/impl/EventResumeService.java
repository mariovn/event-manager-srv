package com.mariovn.eventmanager.service.impl;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.mariovn.eventmanager.domain.Event;
import com.mariovn.eventmanager.domain.Expense;
import com.mariovn.eventmanager.domain.User;

@Service
public class EventResumeService {

	private static final String DATE_FORMAT = "dd-MM-yyyy";

	// Fuentes utilizadas
	private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.COURIER, 18, BaseColor.BLACK);
	private static final Font PARAGRAPH_FONT = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
	
	private final Logger log = LoggerFactory.getLogger(EventResumeService.class);
	
	/**
	 * Método que compone el pdf de resumen del evento a partir de todos los datos
	 * @param document documento a completar.
	 * @param event evento selecionado.
	 * @param userExpenses gastos del usuario en el evento.
	 * @param user usuario.
	 * @throws DocumentException si ocurre algún error generando el documento.
	 */
	public void fillDocumentContent(Document document, Event event, Set<Expense> userExpenses, User user) throws DocumentException {
		this.eventTitleAndDescription(document, event, user);
		
		this.eventExpenseTable(document, event, userExpenses);
						
		this.eventTickets(document, userExpenses);
		
	}
	
	/**
	 * Método que compone la primera parte del documento.
	 * 
	 * @param document documento base
	 * @param event evento seleccionado
	 * @param user usuario
	 * @throws DocumentException
	 */
	private void eventTitleAndDescription(Document document, Event event,  User user)
			throws DocumentException {
		
		// Título
		document.add(new Paragraph(event.getName(), TITLE_FONT));
		
		document.add(new Paragraph(" "));
		
		// usuario
		document.add(new Paragraph((user.getFirstName() != null ? user.getFirstName() + " " : "" )
				+ (user.getLastName() != null ? user.getLastName() + " " : "" )
				+ "email: " + user.getEmail(), PARAGRAPH_FONT));
		
		document.add(new Paragraph(" "));
		
		// Descripción del evento
		document.add(new Paragraph(event.getDescription(), PARAGRAPH_FONT));
		
		document.add(new Paragraph(" "));
	}
	
	/**
	 * Método que añade la información de gastos del documento.
	 * @param document documento base
	 * @param event evento seleccionado.
	 * @param userExpenses gastos del usuario
	 * @throws DocumentException
	 */
	private void eventExpenseTable(Document document, Event event, Set<Expense> userExpenses) throws DocumentException {
		
		Paragraph tableParagraph = new Paragraph();
		
		PdfPTable table = new PdfPTable(new float[] {1, 1, 3, 1, 1});
		
		// Cabeceras
		PdfPCell c1 = new PdfPCell(new Phrase("Evento"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		
		PdfPCell c2 = new PdfPCell(new Phrase("Fecha"));
		c2.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c2);
		
		PdfPCell c3 = new PdfPCell(new Phrase("Descripción"));
		c3.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c3);
		
		PdfPCell c4 = new PdfPCell(new Phrase("Coste Original"));
		c4.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c4);
		
		PdfPCell c5 = new PdfPCell(new Phrase("Coste Evento"));
		c5.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c5);
		table.setHeaderRows(1);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneId.systemDefault());
		
		for (Expense expense : userExpenses) {
			table.addCell(expense.getName());
			table.addCell(formatter.format(expense.getDate()).toString());
			table.addCell(expense.getDescription());
			table.addCell(expense.getOriginalCost() + " " + expense.getCurrencyType().toString());
			table.addCell(expense.getCost() + " " + event.getCurrency().toString());
		}
		
		tableParagraph.add(table);
		
		document.add(tableParagraph);
		
		document.add(new Paragraph(" "));
	}
	
	/**
	 * Método que añade los tickets de los eventos al documento dado.
	 * 
	 * @param document documento base al que se quieren añadir los tickets
	 * @param userExpenses gastos
	 * @throws DocumentException 
	 */
	private void eventTickets(Document document, Set<Expense> userExpenses) throws DocumentException {
		
		for (Expense expense : userExpenses) {
			
			if (expense.getTicket() != null) {
				Paragraph paragraph = new Paragraph(expense.getName(), PARAGRAPH_FONT);
				
				paragraph.add(new Paragraph(" "));
				
				try {
					paragraph.add(Image.getInstance(expense.getTicket()));
					
					document.newPage();
					document.add(paragraph);
				} catch (IOException e) {
					log.error("Error al procesar la imagen");
				}
			}
		}
	}

}
