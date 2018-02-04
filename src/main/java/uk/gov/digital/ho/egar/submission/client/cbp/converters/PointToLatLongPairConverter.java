package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import com.fincore.cbp.stt.xml.T_LatLongPair;
import uk.gov.digital.ho.egar.submission.model.Point;

import java.util.Optional;

public interface PointToLatLongPairConverter {
    Optional<T_LatLongPair> convert(Point point);
}
