package uk.gov.digital.ho.egar.submission.client.cbp.converters.impl;

import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fincore.cbp.stt.xml.T_ContactNumber;
import com.fincore.cbp.stt.xml.T_TelephoneNumberType;

import uk.gov.digital.ho.egar.submission.client.cbp.converters.StringToContactNumberConverter;

@Component
public class StringToContactNumberConverterImpl implements Converter<String, Optional<T_ContactNumber>>, StringToContactNumberConverter {
	
	@Override
	public Optional<T_ContactNumber> convert(String contactNumber) {
		T_ContactNumber contactNum = null;
		if (contactNumber!= null && !contactNumber.trim().isEmpty()) {
			contactNum = new T_ContactNumber();
			contactNum.setTelephoneNumber(contactNumber);
	        contactNum.setTelephoneNumberType(T_TelephoneNumberType.MOBILE);
		}
        return Optional.of(contactNum);
	}
}

