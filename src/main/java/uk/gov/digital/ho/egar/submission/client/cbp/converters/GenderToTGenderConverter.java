package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import com.fincore.cbp.stt.xml.T_Gender;
import uk.gov.digital.ho.egar.submission.model.Gender;

public interface GenderToTGenderConverter {
    T_Gender convert(Gender gender);
}
