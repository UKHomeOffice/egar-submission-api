package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import com.fincore.cbp.stt.xml.T_TravelDocument;
import uk.gov.digital.ho.egar.submission.model.PeopleDetails;

import java.util.Optional;

public interface PeopleDetailsToTravelDocumentConverter {
    Optional<T_TravelDocument> convert(PeopleDetails details);
}
