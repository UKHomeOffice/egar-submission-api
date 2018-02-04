package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.util.Optional;

public interface LocalDateTimeToXMLGregorianConverter {
    Optional<XMLGregorianCalendar> convert(LocalDateTime dateTime);
}
