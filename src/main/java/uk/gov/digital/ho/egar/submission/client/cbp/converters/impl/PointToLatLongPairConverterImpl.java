package uk.gov.digital.ho.egar.submission.client.cbp.converters.impl;

import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fincore.cbp.stt.xml.T_LatLongPair;

import uk.gov.digital.ho.egar.submission.client.cbp.converters.PointToLatLongPairConverter;
import uk.gov.digital.ho.egar.submission.utils.SexagecimalUtils;
import uk.gov.digital.ho.egar.submission.constants.LatitudeLongitudeRegex;
import uk.gov.digital.ho.egar.submission.model.Point;
import uk.gov.digital.ho.egar.submission.model.rest.PointPojo;

@Component
public class PointToLatLongPairConverterImpl implements Converter<Point, Optional<T_LatLongPair>>, PointToLatLongPairConverter {

    private final SexagecimalUtils sexagecimalUtils;

    public PointToLatLongPairConverterImpl(@Autowired final SexagecimalUtils sexagecimalUtils) {
        this.sexagecimalUtils = sexagecimalUtils;
    }

    @Override
	public Optional<T_LatLongPair> convert(Point point) {
		T_LatLongPair retVal = null;

		//Checks that the point is valid
        if (PointPojo.isPointValid(point)) {
            retVal = new T_LatLongPair();

            String latitude = point.getLatitude();
            if (Pattern.matches(LatitudeLongitudeRegex.LATITUDE_DECIMAL_REGEX, point.getLatitude())) {
                double decimalLatitude = Double.valueOf(point.getLatitude());
                latitude = (sexagecimalUtils.coordToDM(decimalLatitude, SexagecimalUtils.Dimension.LAT));
            }
            retVal.setLat(latitude);


            String longitude = point.getLongitude();
            if (Pattern.matches(LatitudeLongitudeRegex.LONGITUDE_DECIMAL_REGEX, point.getLongitude())) {
                double decimalLongitude = Double.valueOf(point.getLongitude());
                longitude = (sexagecimalUtils.coordToDM(decimalLongitude, SexagecimalUtils.Dimension.LONG));
            }
            retVal.setLong(longitude);
        }
        return Optional.ofNullable(retVal);
	}
}
