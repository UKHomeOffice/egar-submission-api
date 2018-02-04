package uk.gov.digital.ho.egar.submission.client.cbp.converters.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fincore.cbp.stt.xml.T_LatLongPair;
import com.fincore.cbp.stt.xml.T_Port;

import uk.gov.digital.ho.egar.submission.client.cbp.converters.AddressBuilder;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.GeographicLocationToPortConverter;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.PointToLatLongPairConverter;
import uk.gov.digital.ho.egar.submission.model.GeographicLocation;

@Component
public class GeographicLocationToPortConverterImpl implements Converter<GeographicLocation,Optional<T_Port>>, GeographicLocationToPortConverter {
	
	private PointToLatLongPairConverter pointToLatLongPairConverter;
	
	@SuppressWarnings("unused")
	private AddressBuilder addressBuilder;

	public GeographicLocationToPortConverterImpl(@Autowired final PointToLatLongPairConverter pointToLatLongPairConverter,
												 @Autowired final AddressBuilder addressBuilder) {
		this.pointToLatLongPairConverter = pointToLatLongPairConverter;
		this.addressBuilder = addressBuilder;
	}

	@Override
	public Optional<T_Port> convert(final GeographicLocation geographicLocation) {
		T_Port returnVal = null;
        if (geographicLocation != null) {
        	returnVal = new T_Port();
        	returnVal.setICAO(geographicLocation.getICAOCode());
        	returnVal.setIATA(geographicLocation.getIATACode());
            Optional<T_LatLongPair> latLong =  pointToLatLongPairConverter.convert(geographicLocation.getPoint());
            if (latLong.isPresent()) {
            	returnVal.setLatLongPair(latLong.get());
            }
//            Optional<T_Address> addr =
//            		addressBuilder.build("", geographicLocation.getPostCode(), "");
//            if (addr.isPresent()) {
//            	returnVal.setFullAddress(addr.get());
//            }
        }
		return Optional.of(returnVal);
	}
}
