package uk.gov.digital.ho.egar.submission.client.cbp.converters.impl;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fincore.cbp.stt.xml.T_Gender;

import uk.gov.digital.ho.egar.submission.client.cbp.converters.GenderToTGenderConverter;
import uk.gov.digital.ho.egar.submission.model.Gender;

@Component
public class GenderToTGenderConverterImpl implements Converter<Gender, T_Gender>, GenderToTGenderConverter {

    @Override
    public T_Gender convert(Gender gender) {
        switch (gender) {
            case MALE:
                return T_Gender.MALE;
            case FEMALE:
                return T_Gender.FEMALE;
            default:
                return T_Gender.INDETERMINATE;
        }
    }
}

