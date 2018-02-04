package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import com.fincore.cbp.stt.xml.T_ContactNumber;

import java.util.Optional;

public interface StringToContactNumberConverter {
    Optional<T_ContactNumber> convert(String contactNumber);
}
