package uk.gov.digital.ho.egar.submission.client.cbp.converters.impl;

import java.util.Optional;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fincore.cbp.stt.xml.T_Address;
import com.fincore.cbp.stt.xml.T_Person;
import com.fincore.cbp.stt.xml.T_TravelDocument;

import uk.gov.digital.ho.egar.submission.client.cbp.converters.*;
import uk.gov.digital.ho.egar.submission.model.People;
import uk.gov.digital.ho.egar.submission.model.PeopleDetails;

@Component
public class PersonBuilderImpl implements PersonBuilder {
	
	private AddressBuilder addressBuilder;
	
	private LocalDateTimeToXMLGregorianConverter dateConverter;
	
	private GenderToTGenderConverter genderConverter;
	
	private PeopleDetailsToTravelDocumentConverter docBuilder;

	public PersonBuilderImpl(@Autowired final AddressBuilder addressBuilder,
							 @Autowired final LocalDateTimeToXMLGregorianConverter dateConverter,
							 @Autowired final GenderToTGenderConverter genderConverter,
							 @Autowired final PeopleDetailsToTravelDocumentConverter docBuilder) {
		this.addressBuilder = addressBuilder;
		this.dateConverter = dateConverter;
		this.genderConverter = genderConverter;
		this.docBuilder = docBuilder;
	}

	@Override
	public Optional<T_Person> builder(final People person, final boolean isCrew) {
		T_Person retVal = null;
		if (person !=null) {
			retVal = new T_Person();
			PeopleDetails details = person.getDetails();
			if (details.getDob()!=null) {
				Optional<XMLGregorianCalendar> xmlDob = dateConverter.convert(details.getDob().atStartOfDay());
				if (xmlDob.isPresent()) {
					retVal.setDateOfbirth(xmlDob.get());
				}
			}
			retVal.getGivenName().add(details.getGivenName());
			retVal.setGender(genderConverter.convert(details.getGender()));
			retVal.setSurname(details.getFamilyName());
			Optional<T_Address> address = addressBuilder.build(details.getAddress(), "", "");
			if (address.isPresent()) {
				retVal.setHomeAddress(address.get());
			}
			retVal.setPlaceOfBirth(details.getPlace());
            retVal.setNationality(details.getNationality());
            Optional<T_TravelDocument> doc = docBuilder.convert(details);
            if(doc.isPresent()) {
            	retVal.getTravelDocument().add(doc.get());
            }
            retVal.setIsCrew(isCrew);
		}
		return Optional.of(retVal);
	}
}

