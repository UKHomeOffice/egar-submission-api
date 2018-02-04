package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.ZonedDateTime;
import java.util.Optional;

public interface ZonedDateTimeToXMLGregorianConverter {
    Optional<XMLGregorianCalendar> convert(ZonedDateTime dateTime);
}
