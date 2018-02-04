package uk.gov.digital.ho.egar.submission;

import static org.junit.Assert.assertNotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.amazonaws.services.s3.AmazonS3;
import com.fincore.cbpcarrierwebservice.entities.File;

import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionApiException;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.ExcelFileBuilder;
import uk.gov.digital.ho.egar.submission.model.Gender;
import uk.gov.digital.ho.egar.submission.model.GeographicLocation;
import uk.gov.digital.ho.egar.submission.model.People;
import uk.gov.digital.ho.egar.submission.model.TravellerType;
import uk.gov.digital.ho.egar.submission.model.jms.QueuedGarSubmissionToCbpPojo;
import uk.gov.digital.ho.egar.submission.model.jms.SubmittingUser;
import uk.gov.digital.ho.egar.submission.model.jms.SubmittingUserPojo;
import uk.gov.digital.ho.egar.submission.model.rest.AircraftRestPojo;
import uk.gov.digital.ho.egar.submission.model.rest.GeographicLocationPojo;
import uk.gov.digital.ho.egar.submission.model.rest.PeopleDetailsPojo;
import uk.gov.digital.ho.egar.submission.model.rest.PeopleRestPojo;
import uk.gov.digital.ho.egar.submission.model.rest.PersonsSubmissionRequestPojo;

@RunWith(SpringRunner.class)
@SpringBootTest(properties
        = {
        "cbp.username=false",
        "cbp.password=false",
        "cbp.url=http://localhost:9046/CarrierWebService/CarrierService.svc",
        "spring.jackson.serialization.write_dates_as_timestamps: false",
        "cbp.cancellation.enabled=false",
        "spring.profiles.active=cbp-mocks,sync,disable-jms,s3-mocks,jms-mocks"
})
@AutoConfigureMockMvc
@Ignore
public class ExcelFileGeneratorTest {

    @Autowired
    private ExcelFileBuilder underTest;

    @MockBean
    private AmazonS3 amazonS3;

    @MockBean
    private JmsTemplate jmsSender;

    //Set this to be a writable directory
    @Value("${file.path}")
    private String filePath;



    @Test
    public void test() throws SubmissionApiException, InvalidFormatException {


        QueuedGarSubmissionToCbpPojo request = createRequest();
        File result = underTest.convertPojoToExcel(request);

        assertNotNull(result);

        byte[] data = result.getData();


        //Generating file at the filepath location
        try (FileOutputStream fos = new FileOutputStream(filePath+result.getFilename())) {
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private QueuedGarSubmissionToCbpPojo createRequest() {
        AircraftRestPojo aircraft = new AircraftRestPojo();
        aircraft.setBase("Test");
        aircraft.setRegistration("ABC123");
        aircraft.setTaxesPaid(true);
        aircraft.setType("chopper");


        List<GeographicLocation> locations = new ArrayList<>();
        GeographicLocationPojo location = new GeographicLocationPojo();
        location.setDatetime(ZonedDateTime.now());
        location.setICAOCode("EGLL");

        GeographicLocationPojo location2 = new GeographicLocationPojo();
        location2.setDatetime(ZonedDateTime.now());
        location2.setICAOCode("EGAR");

        locations.add(location);
        locations.add(location2);


        SubmittingUser userValues = SubmittingUserPojo.builder()
            .userUuid(UUID.randomUUID())
                .email("")
                .build();

        PersonsSubmissionRequestPojo persons = new PersonsSubmissionRequestPojo();


        persons.setCaptain(createPerson(TravellerType.CAPTAIN, 0));
        List<People> crew = new ArrayList<>();
        for (int i = 0; i < 10; i ++){
            crew.add(createPerson(TravellerType.CREW, i));
        }

        List<People> passengers = new ArrayList<>();
        for (int i = 0; i < 20; i ++){
            passengers.add(createPerson(TravellerType.PASSENGER, i));
        }

        persons.setCrew(crew);
        persons.setPassengers(passengers);


        return QueuedGarSubmissionToCbpPojo.builder()
                .aircraft(aircraft)
                .locations(locations)
                .garUuid(UUID.randomUUID())
                .submissionUuid(UUID.randomUUID())
                .submittingUser(userValues)
                .persons(persons)
                .build();
    }

    private PeopleRestPojo createPerson(TravellerType type, int index){
        PeopleRestPojo captain = new PeopleRestPojo();
        captain.setType(type);
        PeopleDetailsPojo captainDetails = new PeopleDetailsPojo();
        captainDetails.setAddress("anc123");
        captainDetails.setDob(LocalDate.now());
        captainDetails.setDocumentExpiryDate(LocalDate.now());
        captainDetails.setDocumentIssuingCountry("UK");
        captainDetails.setDocumentNo("123455");
        captainDetails.setDocumentType("passport");
        captainDetails.setFamilyName(type==TravellerType.CAPTAIN?"captain" + index:"test" + index);
        captainDetails.setGivenName("user");
        captainDetails.setGender(Gender.MALE);
        captainDetails.setNationality("UK");
        captainDetails.setPlace("place");
        captain.setDetails(captainDetails);
        return captain;
    }

}