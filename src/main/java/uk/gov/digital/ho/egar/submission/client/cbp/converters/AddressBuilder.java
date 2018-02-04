package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import java.util.Optional;

import com.fincore.cbp.stt.xml.T_Address;

public interface AddressBuilder {
    Optional<T_Address> build(String address, String postCode, String country);
}
