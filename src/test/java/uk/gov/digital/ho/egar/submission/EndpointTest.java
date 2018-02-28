package uk.gov.digital.ho.egar.submission;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.digital.ho.egar.constants.ServicePathConstants;
import uk.gov.digital.ho.egar.submission.api.RestConstants;
import uk.gov.digital.ho.egar.submission.client.cbp.CbpClient;
import uk.gov.digital.ho.egar.submission.constants.QueueNames;
import uk.gov.digital.ho.egar.submission.model.QueuedGarSubmissionToCbp;
import uk.gov.digital.ho.egar.submission.model.SubmissionStatus;
import uk.gov.digital.ho.egar.submission.model.jms.QueuedGarCancellationToCbpPojo;
import uk.gov.digital.ho.egar.submission.service.repository.SubmittedGarPersistedRecordRepository;
import uk.gov.digital.ho.egar.submission.service.repository.model.SubmittedGarPersistedRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.digital.ho.egar.submission.tests.utils.FileReaderUtils.readFileAsString;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringRunner.class)
@SpringBootTest(properties
        = {
        "cbp.username=false",
        "cbp.password=false",
        "cbp.url=http://localhost:9046/CarrierWebService/CarrierService.svc",
        "spring.jackson.serialization.write_dates_as_timestamps: false",
        "cbp.cancellation.enabled=false",
        "spring.profiles.active=cbp-mocks,sync,s3-mocks,jms-mocks"
})
@AutoConfigureMockMvc
public class EndpointTest {

    private static final String EMAIL_HEADER = "x-auth-email";
    private static final String FORENAME_HEADER = "x-auth-given-name";
    private static final String SURNAME_HEADER = "x-auth-family-name";
    private static final String CONTACT_HEADER = "x-auth-contact-number";
    private static final String ALTERNATIVE_CONTACT_HEADER = "x-auth-alt-contact-number";
    private static final String USERID_HEADER = "x-auth-subject";
    private static final String AUTH_HEADER = "Authorization";
    
    private static final String PATH_BULK = "/api/v1/Submission/Summaries";

    @Autowired
    private SubmittedGarPersistedRecordRepository repo;

    @Autowired
    private SubmissionApplication app;

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private QueueNames queueNames;

    @MockBean
    private AmazonS3 amazonS3;

    @MockBean
    private JmsTemplate jmsTemplate;

    @Test
    public void contextLoads() {
        assertThat(app).isNotNull();
    }

    @Test
    public void successfulSubmissionTest() throws Exception {
        repo.deleteAll();

        UUID userUuid = UUID.randomUUID();

        String jsonRep = readFileAsString("files/SuccessfulRequest.json");
        this.mockMvc
                .perform(post(RestConstants.ROOT_PATH + RestConstants.PATH_SUBMIT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(EMAIL_HEADER, "email")
                        .header(FORENAME_HEADER, "forename")
                        .header(SURNAME_HEADER, "surname")
                        .header(CONTACT_HEADER, "number1")
                        .header(ALTERNATIVE_CONTACT_HEADER, "number2")
                        .header(AUTH_HEADER, "TEST")
                        .header(USERID_HEADER, userUuid)
                        .content(jsonRep))
                .andDo(print())
                .andExpect(status().isSeeOther());

        List<SubmittedGarPersistedRecord> allRecords = repo.findAll();
        assertTrue(allRecords.size() == 1);
        assertEquals(allRecords.get(0).getGarUuid(), (UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71")));
        assertEquals(allRecords.get(0).getUserUuid(), (userUuid));
        assertTrue(allRecords.get(0).getStatus() == SubmissionStatus.PENDING);

        verify(jmsTemplate, times(1)).convertAndSend(eq(queueNames.getCbpSubmissionRequestQueueName()), any(QueuedGarSubmissionToCbp.class));

    }

    @Test
    public void alreadySubmittedTest() throws Exception {
        repo.deleteAll();


        UUID userUuid = UUID.randomUUID();

        SubmittedGarPersistedRecord record = SubmittedGarPersistedRecord.builder()
                .submissionUuid(UUID.randomUUID())
                .garUuid(UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71"))
                .userUuid(userUuid)
                .status(SubmissionStatus.SUBMITTED)
                .build();
        repo.saveAndFlush(record);

        String jsonRep = readFileAsString("files/AlreadySubmittedRequest.json");
        this.mockMvc
                .perform(post(RestConstants.ROOT_PATH + RestConstants.PATH_SUBMIT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(EMAIL_HEADER, "email")
                        .header(FORENAME_HEADER, "forename")
                        .header(SURNAME_HEADER, "surname")
                        .header(CONTACT_HEADER, "number1")
                        .header(ALTERNATIVE_CONTACT_HEADER, "number2")
                        .header(AUTH_HEADER, "TEST")
                        .header(USERID_HEADER, userUuid)
                        .content(jsonRep))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void alreadyPendingRequest() throws Exception {
        repo.deleteAll();


        UUID userUuid = UUID.randomUUID();

        SubmittedGarPersistedRecord record = SubmittedGarPersistedRecord.builder()
                .submissionUuid(UUID.randomUUID())
                .garUuid(UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71"))
                .userUuid(userUuid)
                .status(SubmissionStatus.PENDING)
                .build();
        repo.saveAndFlush(record);

        String jsonRep = readFileAsString("files/AlreadySubmittedRequest.json");
        this.mockMvc
                .perform(post(RestConstants.ROOT_PATH + RestConstants.PATH_SUBMIT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(EMAIL_HEADER, "email")
                        .header(FORENAME_HEADER, "forename")
                        .header(SURNAME_HEADER, "surname")
                        .header(CONTACT_HEADER, "number1")
                        .header(ALTERNATIVE_CONTACT_HEADER, "number2")
                        .header(AUTH_HEADER, "TEST")
                        .header(USERID_HEADER, userUuid)
                        .content(jsonRep))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void alreadyFailedTest() throws Exception {
        repo.deleteAll();


        UUID userUuid = UUID.randomUUID();

        SubmittedGarPersistedRecord record = SubmittedGarPersistedRecord.builder()
                .submissionUuid(UUID.randomUUID())
                .garUuid(UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71"))
                .userUuid(userUuid)
                .status(SubmissionStatus.FAILED)
                .build();
        repo.saveAndFlush(record);

        String jsonRep = readFileAsString("files/AlreadySubmittedRequest.json");
        this.mockMvc
                .perform(post(RestConstants.ROOT_PATH + RestConstants.PATH_SUBMIT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(EMAIL_HEADER, "email")
                        .header(FORENAME_HEADER, "forename")
                        .header(SURNAME_HEADER, "surname")
                        .header(CONTACT_HEADER, "number1")
                        .header(ALTERNATIVE_CONTACT_HEADER, "number2")
                        .header(AUTH_HEADER, "TEST")
                        .header(USERID_HEADER, userUuid)
                        .content(jsonRep))
                .andDo(print())
                .andExpect(status().isSeeOther());
    }


    //Submission failure
    @Test
    public void failedSubmissionTest() throws Exception {
        repo.deleteAll();

        UUID userUuid = UUID.randomUUID();

        String jsonRep = readFileAsString("files/FailedRequest.json");
        this.mockMvc
                .perform(post(RestConstants.ROOT_PATH + RestConstants.PATH_SUBMIT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(EMAIL_HEADER, "email")
                        .header(FORENAME_HEADER, "forename")
                        .header(SURNAME_HEADER, "surname")
                        .header(CONTACT_HEADER, "number1")
                        .header(ALTERNATIVE_CONTACT_HEADER, "number2")
                        .header(AUTH_HEADER, "TEST")
                        .header(USERID_HEADER, userUuid)
                        .content(jsonRep))
                .andDo(print())
                .andExpect(status().isSeeOther());

        List<SubmittedGarPersistedRecord> allRecords = repo.findAll();
        assertTrue(allRecords.size() == 1);
        assertEquals(allRecords.get(0).getGarUuid(), (UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71")));
        assertEquals(allRecords.get(0).getUserUuid(), (userUuid));
        assertTrue(allRecords.get(0).getStatus() == SubmissionStatus.PENDING);
    }

    @Test
    public void successfulRetrieveSubmission() throws Exception {
        repo.deleteAll();

        UUID userUuid = UUID.randomUUID();
        UUID submissionUuid = UUID.randomUUID();
        UUID garUuid = UUID.randomUUID();
        SubmittedGarPersistedRecord record = SubmittedGarPersistedRecord.builder()
                .submissionUuid(submissionUuid)
                .garUuid(garUuid)
                .userUuid(userUuid)
                .status(SubmissionStatus.SUBMITTED)
                .externalSubmissionRef("REFERENCE")
                .externalSubmissionReason("REASON")
                .submissionType("CBP_STT")
                .build();
        repo.saveAndFlush(record);

        this.mockMvc
                .perform(get(RestConstants.ROOT_PATH + ServicePathConstants.ROOT_PATH_SEPERATOR + submissionUuid + ServicePathConstants.ROOT_PATH_SEPERATOR)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(USERID_HEADER, userUuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.gar_uuid", is(garUuid.toString())))
                .andExpect(jsonPath("$.user_uuid", is(userUuid.toString())))
                .andExpect(jsonPath("$.submission.submission_uuid", is(submissionUuid.toString())))
                .andExpect(jsonPath("$.submission.type", is("CBP_STT")))
                .andExpect(jsonPath("$.submission.external_ref", is("REFERENCE")))
                .andExpect(jsonPath("$.submission.reason", is("REASON")))
                .andExpect(jsonPath("$.submission.status", is("SUBMITTED")));
    }

    @Test
    public void cancelSubmissionDoesNotExist() throws Exception {
        UUID userUuid = UUID.randomUUID();
        UUID submissionUuid = UUID.randomUUID();

        String jsonRep = readFileAsString("files/FailedRequest.json");

        this.mockMvc
                .perform(delete(RestConstants.ROOT_PATH + RestConstants.PATH_PREVIOUS_SUBMISSION_PATH.replace("{submission_uuid}", submissionUuid.toString()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(AUTH_HEADER, "TEST")
                        .header(USERID_HEADER, userUuid)
                        .content(jsonRep))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void cancelSubmissionNotInCorrectState() throws Exception {
        UUID userUuid = UUID.randomUUID();
        UUID submissionUuid = UUID.randomUUID();
        UUID garUuid = UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71");
        SubmittedGarPersistedRecord record = SubmittedGarPersistedRecord.builder()
                .submissionUuid(submissionUuid)
                .garUuid(garUuid)
                .userUuid(userUuid)
                .status(SubmissionStatus.PENDING)
                .externalSubmissionRef("REFERENCE")
                .externalSubmissionReason("REASON")
                .submissionType("CBP_STT")
                .build();
        repo.saveAndFlush(record);

        this.mockMvc
                .perform(delete(RestConstants.ROOT_PATH + RestConstants.PATH_PREVIOUS_SUBMISSION_PATH.replace("{submission_uuid}", submissionUuid.toString()))
                        .header(AUTH_HEADER, "TEST")
                        .header(USERID_HEADER, userUuid)
                        .header(EMAIL_HEADER, "test"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void cancelSubmissionOntoQueue() throws Exception {
        repo.deleteAll();

        UUID userUuid = UUID.randomUUID();
        UUID submissionUuid = UUID.randomUUID();
        UUID garUuid = UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71");
        SubmittedGarPersistedRecord record = SubmittedGarPersistedRecord.builder()
                .submissionUuid(submissionUuid)
                .garUuid(garUuid)
                .userUuid(userUuid)
                .status(SubmissionStatus.SUBMITTED)
                .externalSubmissionRef("REFERENCE")
                .externalSubmissionReason("REASON")
                .submissionType("CBP_STT")
                .build();
        repo.saveAndFlush(record);

        this.mockMvc
                .perform(delete(RestConstants.ROOT_PATH + RestConstants.PATH_PREVIOUS_SUBMISSION_PATH.replace("{submission_uuid}", submissionUuid.toString()))
                        .header(AUTH_HEADER, "TEST")
                        .header(USERID_HEADER, userUuid)
                        .header(EMAIL_HEADER, "test"))
                .andDo(print())
                .andExpect(status().isSeeOther());

        List<SubmittedGarPersistedRecord> allRecords = repo.findAll();
        assertTrue(allRecords.size() == 1);
        assertEquals(allRecords.get(0).getGarUuid(), (UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71")));
        assertEquals(allRecords.get(0).getUserUuid(), (userUuid));
        assertTrue(allRecords.get(0).getStatus() == SubmissionStatus.PENDING);

        verify(jmsTemplate, times(1)).convertAndSend(eq(queueNames.getCbpCancelRequestQueueName()), any(QueuedGarCancellationToCbpPojo.class));
    }


    //Validation checks

    @Test

    public void aircraftValidationChecks() throws Exception {

        String json = readFileAsString("files/validation/AircraftIsEmpty.json");
        String resp = badRequestResponseContent(json);
        with(resp).assertThat("$.message[0]", equalTo("aircraft: may not be null"));

        json = readFileAsString("files/validation/AircraftDataMissing.json");
        resp = badRequestResponseContent(json);
        with(resp).assertThat("$.message[*]", hasItems("aircraft.registration: may not be null",
                "aircraft.base: may not be null",
                "aircraft.type: may not be null",
                "aircraft.taxesPaid: may not be null"));

    }


    @Test

    public void locationValidationChecks() throws Exception {

        //Locations as a collection validation rules
        String request = readFileAsString("files/validation/LocationsOnlyOne.json");
        String resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[0]", equalTo("locations: size must be 2 or more"));


        request = readFileAsString("files/validation/LocationsOnlyOnePopulated.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[0]", equalTo("locations[1]: may not be null"));


        request = readFileAsString("files/validation/LocationsAreMissing.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[0]", equalTo("locations: may not be null"));


        //individual location validations

        request = readFileAsString("files/validation/LocationsInvalidLatLong.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[*]", hasItems("locations[1].point.longitude: Longitude does not match the decimal or degrees minutes formats.",
                "locations[1].point.latitude: Latitude does not match the decimal or degrees minutes formats."));


        request = readFileAsString("files/validation/LocationsEmptyData.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[*]", hasItems("locations[1].datetime: may not be null",
                "locations[1]: 0 locations are present, requires 1"));


        request = readFileAsString("files/validation/LocationsTooManyLocations.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[0]", equalTo("locations[1]: 2 locations are present, requires 1"));


        request = readFileAsString("files/validation/LocationsInvalidICAO.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[*]", hasItems("locations[1].ICAOCode: Airport ICAO is not the correct format.", "locations[1].ICAOCode: size must be between 0 and 13"));

    }


    @Test

    public void peopleValidationChecks() throws Exception {

        String request = readFileAsString("files/validation/PersonsAreMissing.json");
        String resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[0]", equalTo("persons: may not be null"));


        request = readFileAsString("files/validation/PersonsNoCaptain.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[0]", equalTo("persons.captain: may not be null"));


        request = readFileAsString("files/validation/PersonsEmptyCaptain.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[*]", hasItems("persons.captain.type: may not be null",
                "persons.captain.details: may not be null"));


        request = readFileAsString("files/validation/PersonsEmptyCaptain.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[*]", hasItems("persons.captain.type: may not be null",
                "persons.captain.details: may not be null"));


        request = readFileAsString("files/validation/PersonsEmptyCaptainDetails.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[*]", hasItems("persons.captain.details.nationality: may not be null",
                "persons.captain.details.documentNo: may not be null",
                "persons.captain.details.givenName: may not be null",
                "persons.captain.details.documentIssuingCountry: may not be null",
                "persons.captain.details.documentType: may not be null",
                "persons.captain.details.documentExpiryDate: may not be null",
                "persons.captain.details.familyName: may not be null",
                "persons.captain.details.dob: may not be null",
                "persons.captain.details.place: may not be null"));


        request = readFileAsString("files/validation/PersonsCaptainDetailsTooLong.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[*]", hasItems("persons.captain.details.nationality: size must be between 0 and 3",
                "persons.captain.details.documentIssuingCountry: size must be between 0 and 3"));


        request = readFileAsString("files/validation/PersonsNullCrewAndPassengers.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[*]", hasItems("persons.passengers[0]: may not be null",
                "persons.crew[0]: may not be null"));


        request = readFileAsString("files/validation/PersonsCrewAndPassengersValidated.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[*]", hasItems("persons.passengers[0].type: may not be null",
                "persons.passengers[0].details: may not be null",
                "persons.crew[0].type: may not be null",
                "persons.crew[0].details: may not be null"));
    }


    @Test
    public void attributesValidationChecks() throws Exception {

        String request = readFileAsString("files/validation/AttributesAreMissing.json");
        String resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[0]", equalTo("attributes: may not be null"));

        request = readFileAsString("files/validation/AttributesEmptyData.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[*]", hasItems("attributes.otherDetails: may not be null",
                "attributes.hazardous: may not be null"));

        request = readFileAsString("files/validation/AttributesResponsiblePersonEmpty.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[0]", equalTo("attributes.otherDetails.type: may not be null"));


        request = readFileAsString("files/validation/AttributesOtherResponsiblePersonFieldsMissing.json");
        resp = badRequestResponseContent(request);
        with(resp).assertThat("$.message[0]", equalTo("attributes.otherDetails: Other responsible person type requires; address; name; contact number"));

    }

    //--------------------------------------------------------------------------------------------------------------------
    
    @Test
	public void shouldOnlyBulkfetchSubmissionsInListAndForThisUser() throws Exception{
		// WITH
		repo.deleteAll();
		UUID userUuid = UUID.randomUUID();

		List<UUID> submissionUuids = new ArrayList<>();

		// add three submissions for the current user and save ids to list
		for (int i=0; i<3 ; i++) {
			UUID submissionUuid = UUID.randomUUID();
			
		SubmittedGarPersistedRecord record = SubmittedGarPersistedRecord.builder()
                .submissionUuid(submissionUuid)
                .garUuid(UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71"))
                .userUuid(userUuid)
                .status(SubmissionStatus.SUBMITTED)
                .build();
        repo.saveAndFlush(record);
			submissionUuids.add(submissionUuid);
		}
		// add a file for user but dont add to list
		UUID submissionUuid = UUID.randomUUID();
			SubmittedGarPersistedRecord record = SubmittedGarPersistedRecord.builder()
                .submissionUuid(submissionUuid)
                .garUuid(UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71"))
                .userUuid(userUuid)
                .status(SubmissionStatus.SUBMITTED)
                .build();
        repo.saveAndFlush(record);
		// add a file for different user
		UUID submissionUuidOther = UUID.randomUUID();
		UUID UuidDiffUser = UUID.randomUUID();
		SubmittedGarPersistedRecord otherRecord = SubmittedGarPersistedRecord.builder()
                .submissionUuid(submissionUuidOther)
                .garUuid(UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71"))
                .userUuid(UuidDiffUser)
                .status(SubmissionStatus.SUBMITTED)
                .build();
        repo.saveAndFlush(otherRecord);
		submissionUuids.add(submissionUuidOther);
		// WHEN
		MvcResult result =
				this.mockMvc
				.perform(post(PATH_BULK).header(USERID_HEADER, userUuid)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.content(mapper.writeValueAsString(submissionUuids)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				//THEN
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[*]submission.submission_uuid", hasItems(submissionUuids.get(0).toString(),submissionUuids.get(1).toString(),submissionUuids.get(2).toString()))).andReturn();
		//Check it doesn't contain other files
		String response = result.getResponse().getContentAsString();
		assertFalse(response.contains(submissionUuid.toString()));
		assertFalse(response.contains(submissionUuids.get(3).toString()));

	}

	@Test
	public void bulkFetchShouldReturnEmptyArrayIfNoMatch() throws Exception{
		// WITH
		repo.deleteAll();
		UUID userUuid       = UUID.randomUUID();
		List<UUID> submissionUuids = new ArrayList<>();
		for (int i=0; i<3 ; i++) {
			submissionUuids.add(UUID.randomUUID());
		}
		// WHEN
		this.mockMvc
		.perform(post(PATH_BULK).header(USERID_HEADER, userUuid)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(mapper.writeValueAsString(submissionUuids)))
		// THEN
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").exists())
		.andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$", hasSize(0)));
	}
	
	@Test
	public void bulkFetchShouldNotContainDuplicateData() throws Exception{
		// WITH
		repo.deleteAll();
		UUID userUuid = UUID.randomUUID();
		UUID submissionUuid  = UUID.randomUUID();
		
		// add gar for user
		SubmittedGarPersistedRecord record = SubmittedGarPersistedRecord.builder()
                .submissionUuid(submissionUuid)
                .garUuid(UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71"))
                .userUuid(userUuid)
                .status(SubmissionStatus.SUBMITTED)
                .build();
        repo.saveAndFlush(record);
		
		List<UUID> submissionUuids = new ArrayList<>();
		// add same file uuid to request list
		submissionUuids.add(submissionUuid);
		submissionUuids.add(submissionUuid);
		// WHEN
		this.mockMvc
		.perform(post(PATH_BULK).header(USERID_HEADER, userUuid)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(mapper.writeValueAsString(submissionUuids)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").exists())
		.andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$", hasSize(1)));
	}
	

    private String badRequestResponseContent(String json) throws Exception {

        UUID userUuid = UUID.randomUUID();
        MvcResult result = this.mockMvc
                .perform(post(RestConstants.ROOT_PATH + RestConstants.PATH_SUBMIT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(EMAIL_HEADER, "email")
                        .header(FORENAME_HEADER, "forename")
                        .header(SURNAME_HEADER, "surname")
                        .header(CONTACT_HEADER, "number1")
                        .header(ALTERNATIVE_CONTACT_HEADER, "number2")
                        .header(AUTH_HEADER, "TEST")
                        .header(USERID_HEADER, userUuid)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        return result.getResponse().getContentAsString();

    }


}
