package uk.gov.digital.ho.egar.submission.client.cbp.converters.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.submission.client.cbp.converters.LocalDateTimeToXMLGregorianConverter;

@Component
public class LocalDateTimeToXMLGregorianConverterImpl 
	implements Converter<LocalDateTime, 
		Optional<XMLGregorianCalendar>>, LocalDateTimeToXMLGregorianConverter {
	private static final String MISSING_TIMESTAMP_VAL = ":00";
	private static Log logger = LogFactory.getLog(LocalDateTimeToXMLGregorianConverterImpl.class);

	@Override
	public Optional<XMLGregorianCalendar> convert(LocalDateTime dateTime) {
		XMLGregorianCalendar xCal = null;
		try {
			String dateInIso = dateTime.toString();
	        if (dateTime.getSecond() == 0 && dateTime.getNano() == 0) {
				dateInIso += MISSING_TIMESTAMP_VAL;
			}
			xCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateInIso);
		} catch (DatatypeConfigurationException e) {
			logger.error(e.getMessage());;
		}
		return Optional.of(xCal);
	}
}
