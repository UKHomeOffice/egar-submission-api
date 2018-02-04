package uk.gov.digital.ho.egar.submission.client.cbp.converters.impl;

import java.util.List;
import java.util.Optional;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fincore.cbp.stt.xml.T_Address;
import com.fincore.cbp.stt.xml.T_CommonSegment;
import com.fincore.cbp.stt.xml.T_Person;
import com.fincore.cbp.stt.xml.T_Port;

import uk.gov.digital.ho.egar.submission.model.GeographicLocation;
import uk.gov.digital.ho.egar.submission.model.People;
import uk.gov.digital.ho.egar.submission.api.exceptions.MissingMandatoryFieldException;
import uk.gov.digital.ho.egar.submission.client.cbp.CbpConstants;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.AddressBuilder;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.AttributesToTRestrictedGoodsConverter;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.BuildCommonSegFromSubmissionRequest;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.GeographicLocationToPortConverter;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.PersonBuilder;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.ZonedDateTimeToXMLGregorianConverter;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionRequest;
import uk.gov.digital.ho.egar.submission.model.PersonsSubmissionRequest;
import uk.gov.digital.ho.egar.submission.model.rest.Attribute;
import uk.gov.digital.ho.egar.submission.model.rest.ResponsiblePersonDetails;

@Component
public class BuildCommonSegFromSubmissionRequestImpl implements BuildCommonSegFromSubmissionRequest {

    private GeographicLocationToPortConverter geographicLocationToPortConverter;

    private AddressBuilder addressBuilder;

    private ZonedDateTimeToXMLGregorianConverter dateTimeConverter;

    private PersonBuilder personBuilder;

    private AttributesToTRestrictedGoodsConverter restrictGoods;

    public BuildCommonSegFromSubmissionRequestImpl(@Autowired final GeographicLocationToPortConverter geographicLocationToPortConverter,
                                                   @Autowired final AddressBuilder addressBuilder,
                                                   @Autowired final ZonedDateTimeToXMLGregorianConverter dateTimeConverter,
                                                   @Autowired final PersonBuilder personBuilder,
                                                   @Autowired final AttributesToTRestrictedGoodsConverter restrictGoods) {
        this.geographicLocationToPortConverter = geographicLocationToPortConverter;
        this.addressBuilder = addressBuilder;
        this.dateTimeConverter = dateTimeConverter;
        this.personBuilder = personBuilder;
        this.restrictGoods = restrictGoods;
    }

    @Override
    public T_CommonSegment build(GarSubmissionRequest garSnapshot) throws MissingMandatoryFieldException {

        if (garSnapshot.getLocations() == null || garSnapshot.getLocations().size() < 2) {
            throw new MissingMandatoryFieldException("Arrival and departure information missing.");
        }

        T_CommonSegment commonSeg = new T_CommonSegment();
        commonSeg.setVoyageEventCode(CbpConstants.DEPARTURE_CONFIRMATION_EVENT_CODE);
        setPortOfArrival(commonSeg, garSnapshot.getLocations().get(1));
        setPortOfDeparture(commonSeg, garSnapshot.getLocations().get(0));
        setCaptainDetailsOnCommonSeg(commonSeg, garSnapshot);
        setResponsiblePersonDetailsOnCommonSeg(commonSeg, garSnapshot);
        addCrewToCommonSeg(commonSeg.getPerson(), garSnapshot.getPersons());
        addPassengersToCommonSeg(commonSeg.getPerson(), garSnapshot.getPersons());
        addRestrictedGoodsIfPresent(commonSeg, garSnapshot.getAttributes());

        return commonSeg;
    }

    private void addRestrictedGoodsIfPresent(T_CommonSegment commonSeg, Attribute attributes) {
        restrictGoods.convert(attributes).ifPresent(commonSeg::setRestrictedGoods);
    }

    private void addPassengersToCommonSeg(List<T_Person> person, PersonsSubmissionRequest persons) {
        if (persons != null && persons.getPassengers() != null) {
            for (People current : persons.getPassengers()) {
                personBuilder.builder(current, false).ifPresent(person::add);
            }
        }
    }

    private void addCrewToCommonSeg(List<T_Person> person, PersonsSubmissionRequest persons) {
        if (persons != null && persons.getCrew() != null) {
            for (People current : persons.getCrew()) {
                personBuilder.builder(current, true).ifPresent(person::add);
            }
        }
    }

    private void setResponsiblePersonDetailsOnCommonSeg(T_CommonSegment commonSeg, GarSubmissionRequest garSnapshot) {

        if (garSnapshot.getAttributes() != null && garSnapshot.getAttributes().getOtherDetails() != null) {
            Attribute attributes = garSnapshot.getAttributes();
            ResponsiblePersonDetails personDetails = attributes.getOtherDetails();

            commonSeg.setOwnerGivenName(personDetails.getName());

            Optional<T_Address> respAddress =
                    addressBuilder.build(personDetails.getAddress(), "", "");
            respAddress.ifPresent(commonSeg::setOwnerAddress);
        }

    }

    private void setCaptainDetailsOnCommonSeg(T_CommonSegment commonSeg, GarSubmissionRequest garSnapshot) {
        if (garSnapshot.getPersons() != null && garSnapshot.getPersons().getCaptain() != null) {
            Optional<T_Person> capt = personBuilder.builder(
                    garSnapshot.getPersons().getCaptain(), true);
            if (!capt.isPresent()) {
                return;
            }
            commonSeg.setCaptainAddress(capt.get().getHomeAddress());
            commonSeg.setCaptainGivenName(capt.get().getGivenName().get(0));
            commonSeg.setCaptainSurname(capt.get().getSurname());
            commonSeg.getPerson().add(capt.get());
        }

    }

    private void setPortOfDeparture(T_CommonSegment commonSeg, GeographicLocation geographicLocation)
            throws MissingMandatoryFieldException {
        Optional<XMLGregorianCalendar> arrivalDate = dateTimeConverter.convert(geographicLocation.getDatetime());
        Optional<T_Port> port = geographicLocationToPortConverter.convert(geographicLocation);
        if (!arrivalDate.isPresent() || !port.isPresent()) {
            throw new MissingMandatoryFieldException("Missing mandatory fields in port of arrival. See departure date, departure lat long.");
        }
        commonSeg.setPortOfCallDeparture(port.get());
        commonSeg.setDepartureDate(arrivalDate.get());
        commonSeg.setDepartureTime(arrivalDate.get());

    }

    private void setPortOfArrival(T_CommonSegment commonSeg, GeographicLocation arrivalGeo)
            throws MissingMandatoryFieldException {
        Optional<XMLGregorianCalendar> arrivalDate = dateTimeConverter.convert(arrivalGeo.getDatetime());
        Optional<T_Port> latLong = geographicLocationToPortConverter.convert(arrivalGeo);
        if (!arrivalDate.isPresent() || !latLong.isPresent()) {
            throw new MissingMandatoryFieldException("Missing mandatory fields in port of arrival. See arrival date, arrival lat long.");
        }
        commonSeg.setPortOfCallArrival(latLong.get());
        commonSeg.setArrivalDate(arrivalDate.get());
        commonSeg.setArrivalTime(arrivalDate.get());
    }

}
