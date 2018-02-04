package uk.gov.digital.ho.egar.submission.api.rest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.digital.ho.egar.submission.model.GeographicLocation;
import uk.gov.digital.ho.egar.submission.model.Gender;
import uk.gov.digital.ho.egar.submission.model.People;
import uk.gov.digital.ho.egar.submission.model.TravellerType;
import uk.gov.digital.ho.egar.submission.api.RestConstants;
import uk.gov.digital.ho.egar.submission.api.rest.GarSubmissionController;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionRequest;
import uk.gov.digital.ho.egar.submission.service.SubmissionService;
import uk.gov.digital.ho.egar.submission.utils.UriLocationUtilities;
import uk.gov.digital.ho.egar.submission.utils.impl.UriLocationUtilitiesImpl;
import uk.gov.digital.ho.egar.submission.model.ResponsiblePersonType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static uk.gov.digital.ho.egar.submission.tests.utils.FileReaderUtils.readFileAsString;

public class GarSubmissionControllerTest {

    public static final String EMAIL_HEADER = "x-auth-email";
    public static final String FORENAME_HEADER = "x-auth-given-name";
    public static final String SURNAME_HEADER = "x-auth-family-name";
    public static final String CONTACT_HEADER = "x-auth-contact-number";
    public static final String ALTERNATIVE_CONTACT_HEADER = "x-auth-alt-contact-number";
    public static final String USERID_HEADER = "x-auth-subject";
    public static final String AUTH_HEADER = "Authorization";

	@Mock
	private SubmissionService service;

    private GarSubmissionController underTest;

    private MockMvc mockMvc;

	@Before
	public void contextLoads() {
        MockitoAnnotations.initMocks(this);

        UriLocationUtilities uriLocationUtilities = new UriLocationUtilitiesImpl();

        underTest = new GarSubmissionController(service, uriLocationUtilities);

        mockMvc = MockMvcBuilders.standaloneSetup(underTest).build();
    }

    @Test
    public void deserialisesCorrectly() throws Exception {
    	String jsonRep = readFileAsString("files/DeserialisationRequest.json");
    	UUID userUuid = UUID.randomUUID();

        this.mockMvc
                .perform(post("http://localhost:8081"+RestConstants.ROOT_PATH + RestConstants.PATH_SUBMIT)
                		.contentType(MediaType.APPLICATION_JSON_VALUE)
                		.header(EMAIL_HEADER, "email")
                        .header(FORENAME_HEADER, "forename")
                        .header(SURNAME_HEADER,"surname")
                        .header(CONTACT_HEADER, "number1")
                        .header(ALTERNATIVE_CONTACT_HEADER, "number2")
                        .header(AUTH_HEADER, "TEST")
                        .header(USERID_HEADER, userUuid)
                        .content(jsonRep));

        ArgumentCaptor<GarSubmissionRequest> captor = ArgumentCaptor.forClass(GarSubmissionRequest.class);

        Mockito.verify(service).submitGar(captor.capture(),any());

        GarSubmissionRequest target = captor.getValue();

        //Top level assertions
        assertThat(target.getGarUuid().equals(UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71")));

        //Aircraft assertions
        assertThat(target.getAircraft().getBase().equals("0AK1"));
        assertThat(target.getAircraft().getRegistration().equals("TEST12345"));
        assertThat(target.getAircraft().getType().equals("Helicopter"));
        assertThat(target.getAircraft().getTaxesPaid());

        //Location assertions
        assertThat(target.getLocations().size()==2);
        GeographicLocation loc1 = target.getLocations().get(0);
        assertThat(loc1.getDatetime().equals(LocalDateTime.of(2017,12,14,16,10,15,42)));
        assertThat(loc1.getICAOCode().equals("0AK0"));
        assertThat(loc1.getLegCount().equals(2));
        assertThat(loc1.getLegNumber().equals(0));

        GeographicLocation loc2 = target.getLocations().get(1);
        assertThat(loc2.getDatetime().equals(LocalDateTime.of(2017,12,14,16,01,15,436)));
        assertThat(loc2.getIATACode().equals("BRS"));
        assertThat(loc2.getLegCount().equals(2));
        assertThat(loc2.getLegNumber().equals(1));

        //People assertions
        People captain = target.getPersons().getCaptain();
        assertThat(captain.getType() == TravellerType.CAPTAIN);
        assertThat(captain.getDetails().getDob().equals(LocalDate.of(2017,12,14)));
        assertThat(captain.getDetails().getGender() == Gender.FEMALE);
        assertThat(captain.getDetails().getAddress().equals("76 Hammersmith Rd"));
        assertThat(captain.getDetails().getPlace().equals("London"));
        assertThat(captain.getDetails().getNationality().equals("GB"));
        assertThat(captain.getDetails().getGivenName().equals("hannah"));
        assertThat(captain.getDetails().getFamilyName().equals("test"));
        assertThat(captain.getDetails().getDocumentType().equals("passport"));
        assertThat(captain.getDetails().getDocumentNo().equals("1234567"));
        assertThat(captain.getDetails().getDocumentExpiryDate().equals(LocalDate.of(2017,12,14)));
        assertThat(captain.getDetails().getDocumentIssuingCountry().equals("UK"));

        People crew = target.getPersons().getCrew().get(0);
        assertThat(crew.getType() == TravellerType.CREW);
        assertThat(crew.getDetails().getDob().equals(LocalDate.of(2017,12,15)));
        assertThat(crew.getDetails().getGender() == Gender.FEMALE);
        assertThat(crew.getDetails().getAddress().equals("77 Hammersmith Rd"));
        assertThat(crew.getDetails().getPlace().equals("Bristol"));
        assertThat(crew.getDetails().getNationality().equals("GB"));
        assertThat(crew.getDetails().getGivenName().equals("hello"));
        assertThat(crew.getDetails().getFamilyName().equals("world"));
        assertThat(crew.getDetails().getDocumentType().equals("passport"));
        assertThat(crew.getDetails().getDocumentNo().equals("zzzzz"));
        assertThat(crew.getDetails().getDocumentExpiryDate().equals(LocalDate.of(2018,12,14)));
        assertThat(crew.getDetails().getDocumentIssuingCountry().equals("DE"));

        People passenger = target.getPersons().getPassengers().get(0);
        assertThat(passenger.getType() == TravellerType.PASSENGER);
        assertThat(passenger.getDetails().getDob().equals(LocalDate.of(2017,12,15)));
        assertThat(passenger.getDetails().getGender() == Gender.MALE);
        assertThat(passenger.getDetails().getAddress().equals("10 Hammersmith Rd"));
        assertThat(passenger.getDetails().getPlace().equals("Edinburgh"));
        assertThat(passenger.getDetails().getNationality().equals("ES"));
        assertThat(passenger.getDetails().getGivenName().equals("happy"));
        assertThat(passenger.getDetails().getFamilyName().equals("day"));
        assertThat(passenger.getDetails().getDocumentType().equals("passport"));
        assertThat(passenger.getDetails().getDocumentNo().equals("jklonasd"));
        assertThat(passenger.getDetails().getDocumentExpiryDate().equals(LocalDate.of(2017,11,14)));
        assertThat(passenger.getDetails().getDocumentIssuingCountry().equals("GB"));

        //Attribute assertions
        assertThat(target.getAttributes().getHazardous());
        assertThat(target.getAttributes().getOtherDetails().getType() == ResponsiblePersonType.OTHER);
        assertThat(target.getAttributes().getOtherDetails().getName().equals("Responsible person"));
        assertThat(target.getAttributes().getOtherDetails().getAddress().equals("abc123"));
        assertThat(target.getAttributes().getOtherDetails().getContactNumber().equals("102930211"));

    }
}
