package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import com.fincore.cbp.stt.xml.T_Port;
import uk.gov.digital.ho.egar.submission.model.GeographicLocation;

import java.util.Optional;

public interface GeographicLocationToPortConverter {
    Optional<T_Port> convert(GeographicLocation geographicLocation);
}
