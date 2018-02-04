package uk.gov.digital.ho.egar.submission.client.cbp.converters.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.fincore.cbp.stt.xml.T_GenericAviation;

import uk.gov.digital.ho.egar.submission.model.Aircraft;
import uk.gov.digital.ho.egar.submission.api.exceptions.MissingMandatoryFieldException;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.GenericAviationBuilder;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionRequest;

@Component
public class GenericAviationBuilderImpl implements GenericAviationBuilder {

	@Override
    public T_GenericAviation build(GarSubmissionRequest garSnapshot) throws MissingMandatoryFieldException {

	    if (garSnapshot.getAircraft() == null){
	        throw new MissingMandatoryFieldException("Mandatory aircraft information is missing from the request.");
        }
        Aircraft garAircraft = garSnapshot.getAircraft();

        if (garAircraft.getRegistration() == null || StringUtils.isEmpty(garAircraft.getRegistration())){
            throw new MissingMandatoryFieldException("Mandatory aircraft registration is missing from the request.");
        }

        T_GenericAviation aircraftCBP  = new T_GenericAviation();
        aircraftCBP.setAircraftRegistrationNumber(garAircraft.getRegistration());
        aircraftCBP.setAircraftType(garAircraft.getType());
        aircraftCBP.setBasedAt(garAircraft.getBase());
        aircraftCBP.setFreeCirculationInEU(garAircraft.getTaxesPaid());

        return aircraftCBP;
	}
}
