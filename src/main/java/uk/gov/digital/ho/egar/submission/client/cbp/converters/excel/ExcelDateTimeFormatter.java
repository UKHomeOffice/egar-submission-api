package uk.gov.digital.ho.egar.submission.client.cbp.converters.excel;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.ZonedDateTime;

public interface ExcelDateTimeFormatter {
	public static String DATE_FORMAT = "dd/MM/yyyy";
	public static String TIME_FORMAT = "hh:mm:ss";
	public String formatXmlDate(XMLGregorianCalendar date);
	public String formatXmlTime(XMLGregorianCalendar date);
	public String formatDate(ZonedDateTime dateTime);
	public String formatTime(ZonedDateTime dateTime);

}
