package uk.gov.digital.ho.egar.submission.client.cbp.converters.impl;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fincore.cbp.stt.xml.T_Address;

import uk.gov.digital.ho.egar.submission.client.cbp.converters.AddressBuilder;

@Component
public class AddressBuilderImpl implements AddressBuilder {
	@Override
	public Optional<T_Address> build(String address, String postCode, String country){
		T_Address retVal = new T_Address();
		boolean populated = false;
		if (postCode != null && !postCode.isEmpty()) {
			populated = true;
			retVal.setAddressPostCode(postCode);
		}
		if (country != null && !country.isEmpty()) {
			populated = true;
			retVal.setAddressCountry(country);
		}
		if (address != null && !address.isEmpty()) {
			populated = true;
			retVal.getAddressField().add(address);
		}
		return populated? Optional.of(retVal): Optional.empty();
	}
}
