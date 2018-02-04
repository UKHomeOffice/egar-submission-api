package uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.ExcelDateTimeFormatter;

@Component
public class ExcelDateFormatterImpl implements ExcelDateTimeFormatter {

	@Override
	public String formatXmlDate(XMLGregorianCalendar date)  {
		DateFormat df = new SimpleDateFormat(ExcelDateTimeFormatter.DATE_FORMAT);
        GregorianCalendar gCalendar = date.toGregorianCalendar();
        Date xmlDateVal = gCalendar.getTime();
        return df.format(xmlDateVal);
	}

	@Override
	public String formatXmlTime(XMLGregorianCalendar date)  {
		DateFormat df = new SimpleDateFormat(ExcelDateTimeFormatter.TIME_FORMAT);
        GregorianCalendar gCalendar = date.toGregorianCalendar();
        Date xmlDateVal = gCalendar.getTime();
        return df.format(xmlDateVal);
	}

	@Override
	public String formatDate(ZonedDateTime dateTime) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern(ExcelDateTimeFormatter.DATE_FORMAT);

		return dateTime.format(df);
	}

	@Override
	public String formatTime(ZonedDateTime dateTime) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern(ExcelDateTimeFormatter.TIME_FORMAT);

		return dateTime.format(df);
	}

}
