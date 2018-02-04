package uk.gov.digital.ho.egar.submission.client.cbp.converters.impl;

import java.util.Optional;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fincore.cbp.stt.xml.T_TravelDocument;

import uk.gov.digital.ho.egar.submission.client.cbp.converters.LocalDateTimeToXMLGregorianConverter;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.PeopleDetailsToTravelDocumentConverter;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.StringToDocumentTypeConverter;
import uk.gov.digital.ho.egar.submission.model.PeopleDetails;

@Component
public class PeopleDetailsToTravelDocumentConverterImpl implements Converter<PeopleDetails, Optional<T_TravelDocument>>, PeopleDetailsToTravelDocumentConverter {
	private StringToDocumentTypeConverter documentTypeConverter;

	private LocalDateTimeToXMLGregorianConverter dateConverter;

	public PeopleDetailsToTravelDocumentConverterImpl(@Autowired final StringToDocumentTypeConverter documentTypeConverter,
													  @Autowired final LocalDateTimeToXMLGregorianConverter dateConverter) {
		this.documentTypeConverter = documentTypeConverter;
		this.dateConverter = dateConverter;
	}

	@Override
	public Optional<T_TravelDocument> convert(PeopleDetails details) {
		T_TravelDocument retVal = null;
		if(details != null) {
			retVal = new T_TravelDocument();
			retVal.setDocumentNumber(details.getDocumentNo());
			retVal.setDocumentType(documentTypeConverter.convert(details.getDocumentType()));
			if (details.getDocumentExpiryDate()!=null) {
				Optional<XMLGregorianCalendar> xmlDate = dateConverter.convert(details.getDocumentExpiryDate().atStartOfDay());
				if (xmlDate.isPresent()) {
					retVal.setExpirationDate(xmlDate.get());
				}
			}
			retVal.setIssuingAuthority(details.getDocumentIssuingCountry());
		}
		return Optional.of(retVal);
	}

}

