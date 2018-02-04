package uk.gov.digital.ho.egar.submission.client.cbp.converters.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.submission.client.cbp.converters.ZonedDateTimeToXMLGregorianConverter;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.Optional;

@Component
public class ZonedDateTimeToXMLGregorianConverterImpl 
	implements Converter<ZonedDateTime, Optional<XMLGregorianCalendar>>, 
	ZonedDateTimeToXMLGregorianConverter 
	{
	private static Log logger = LogFactory.getLog(ZonedDateTimeToXMLGregorianConverterImpl.class);
	@Override
	public Optional<XMLGregorianCalendar> convert(ZonedDateTime dateTime) {
		XMLGregorianCalendar xCal = null;
		try {
			GregorianCalendar gregorianCalendar = GregorianCalendar.from(dateTime);
			xCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		} catch (DatatypeConfigurationException e) {
			logger.error(e.getMessage());;
		}
		return Optional.ofNullable(xCal);
	}
}
