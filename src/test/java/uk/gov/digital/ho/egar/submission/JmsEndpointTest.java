package uk.gov.digital.ho.egar.submission;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.digital.ho.egar.submission.tests.utils.FileReaderUtils.readFileAsString;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.digital.ho.egar.submission.api.jms.SubmissionJmsService;
import uk.gov.digital.ho.egar.submission.client.cbp.CbpClient;
import uk.gov.digital.ho.egar.submission.client.cbp.mock.MockCbpClient;
import uk.gov.digital.ho.egar.submission.model.SubmissionStatus;
import uk.gov.digital.ho.egar.submission.model.jms.QueuedGarCancellationToCbpPojo;
import uk.gov.digital.ho.egar.submission.model.jms.QueuedGarSubmissionToCbpPojo;
import uk.gov.digital.ho.egar.submission.model.jms.SubmittingUser;
import uk.gov.digital.ho.egar.submission.model.jms.SubmittingUserPojo;
import uk.gov.digital.ho.egar.submission.service.repository.SubmittedGarPersistedRecordRepository;
import uk.gov.digital.ho.egar.submission.service.repository.model.SubmittedGarPersistedRecord;

@RunWith(SpringRunner.class)
@SpringBootTest(properties
        ={
        "cbp.username=false",
        "cbp.password=false",
        "cbp.url=http://localhost:9046/CarrierWebService/CarrierService.svc",
        "spring.jackson.serialization.write_dates_as_timestamps: false",
		"cbp.cancellation.enabled=false",
        "spring.profiles.active=cbp-mocks,sync,s3-mocks,jms-mocks"
})
@AutoConfigureMockMvc
public class JmsEndpointTest {

	@Autowired
    private SubmittedGarPersistedRecordRepository repo;

    @Autowired
    private SubmissionJmsService underTest;

    @Autowired
    private CbpClient cbpClient;

    @MockBean
    private AmazonS3 amazonS3;

    @MockBean
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void successfulSubmissionTest() throws Exception {
        repo.deleteAll();

        UUID userUuid = UUID.randomUUID();
        UUID submissionUuid = UUID.randomUUID();

        //Create a pending record
        repo.saveAndFlush(SubmittedGarPersistedRecord.builder()
                .garUuid(UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71"))
                .status(SubmissionStatus.PENDING)
               .userUuid(userUuid)
                .submissionUuid(submissionUuid)
                .build());

	    setMockCbpClientSuccess(true, false);


	    //Using the successful request
    	String jsonRep = readFileAsString("files/SuccessfulRequest.json");
        QueuedGarSubmissionToCbpPojo request = objectMapper.readValue(jsonRep, QueuedGarSubmissionToCbpPojo.class);
        //set submission id
        request.setSubmissionUuid(submissionUuid);

        //Se user values
        SubmittingUser user = SubmittingUserPojo.builder()
                .userUuid(userUuid)
                .email("")
                .build();
        request.setSubmittingUser(user);

        //WHEN
        underTest.processSubmission(objectMapper.writeValueAsString(request));

        //THEN
        List<SubmittedGarPersistedRecord> allRecords = repo.findAll();
        assertTrue(allRecords.size()==1);
        assertEquals(allRecords.get(0).getGarUuid(),(UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71")));
        assertEquals(allRecords.get(0).getUserUuid(),(userUuid));
        assertTrue(allRecords.get(0).getStatus()==SubmissionStatus.SUBMITTED);

        MockCbpClient clientMock = (MockCbpClient)cbpClient;

        Document xml = parse(clientMock.getLastRequest());
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/VoyageEventCode", equalTo("DC")).matches(xml));

        //Departure
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/DepartureDate", equalTo("2017-12-14Z")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/DepartureTime", equalTo("16:00:15.420Z")).matches(xml));

        //PortOfCallDeparture
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/PortOfCallDeparture/ICAO", equalTo("EGAA")).matches(xml));

        //Arrival
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/ArrivalDate", equalTo("2017-12-14Z")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/ArrivalTime", equalTo("16:00:15.436Z")).matches(xml));

        //PortOfCallArrival
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/PortOfCallArrival/IATA", equalTo("BRS")).matches(xml));

        //Captain
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/CaptainSurname", equalTo("captain")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/CaptainGivenName", equalTo("test1")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/CaptainAddress/AddressField", equalTo("76 Hammersmith Rd")).matches(xml));

        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[1]/GivenName", equalTo("test1")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[1]/Surname", equalTo("captain")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[1]/IsCrew", equalTo("true")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[1]/Gender", equalTo("Female")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[1]/HomeAddress/AddressField[1]", equalTo("76 Hammersmith Rd")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[1]/DateOfbirth", equalTo("2017-12-14")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[1]/PlaceOfBirth", equalTo("London")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[1]/Nationality", equalTo("GB")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[1]/TravelDocument/DocumentType", equalTo("Passport")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[1]/TravelDocument/DocumentNumber", equalTo("123456")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[1]/TravelDocument/IssuingAuthority", equalTo("GB")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[1]/TravelDocument/ExpirationDate", equalTo("2017-07-14")).matches(xml));

        //Crew 1
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[2]/GivenName", equalTo("test2")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[2]/Surname", equalTo("crew1")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[2]/IsCrew", equalTo("true")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[2]/Gender", equalTo("Male")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[2]/HomeAddress/AddressField[1]", equalTo("77 Hammersmith Rd")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[2]/DateOfbirth", equalTo("2017-12-14")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[2]/PlaceOfBirth", equalTo("London")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[2]/Nationality", equalTo("NL")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[2]/TravelDocument/DocumentType", equalTo("Passport")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[2]/TravelDocument/DocumentNumber", equalTo("abc123")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[2]/TravelDocument/IssuingAuthority", equalTo("NL")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[2]/TravelDocument/ExpirationDate", equalTo("2017-08-14")).matches(xml));

        //Crew 2
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[3]/GivenName", equalTo("test3")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[3]/Surname", equalTo("crew2")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[3]/IsCrew", equalTo("true")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[3]/Gender", equalTo("Indeterminate")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[3]/HomeAddress/AddressField[1]", equalTo("78 Hammersmith Rd")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[3]/DateOfbirth", equalTo("2017-12-14")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[3]/PlaceOfBirth", equalTo("London")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[3]/Nationality", equalTo("FR")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[3]/TravelDocument/DocumentType", equalTo("IdentityCard")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[3]/TravelDocument/DocumentNumber", equalTo("kasasa")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[3]/TravelDocument/IssuingAuthority", equalTo("FR")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[3]/TravelDocument/ExpirationDate", equalTo("2017-09-14")).matches(xml));

        //Pass 1
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[4]/GivenName", equalTo("test4")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[4]/Surname", equalTo("pass1")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[4]/IsCrew", equalTo("false")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[4]/Gender", equalTo("Female")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[4]/HomeAddress/AddressField[1]", equalTo("79 Hammersmith Rd")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[4]/DateOfbirth", equalTo("2017-12-14")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[4]/PlaceOfBirth", equalTo("London")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[4]/Nationality", equalTo("DE")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[4]/TravelDocument/DocumentType", equalTo("Other")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[4]/TravelDocument/DocumentNumber", equalTo("asdadasdgg")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[4]/TravelDocument/IssuingAuthority", equalTo("DE")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[4]/TravelDocument/ExpirationDate", equalTo("2017-10-14")).matches(xml));

        //Pass 2
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[5]/GivenName", equalTo("test5")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[5]/Surname", equalTo("pass2")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[5]/IsCrew", equalTo("false")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[5]/Gender", equalTo("Female")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[5]/HomeAddress/AddressField[1]", equalTo("80 Hammersmith Rd")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[5]/DateOfbirth", equalTo("2017-12-14")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[5]/PlaceOfBirth", equalTo("London")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[5]/Nationality", equalTo("ES")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[5]/TravelDocument/DocumentType", equalTo("Passport")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[5]/TravelDocument/DocumentNumber", equalTo("asdasdsa")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[5]/TravelDocument/IssuingAuthority", equalTo("ES")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/Person[5]/TravelDocument/ExpirationDate", equalTo("2017-11-14")).matches(xml));

        //Responsible
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/OwnerGivenName", equalTo("Responsible person")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/OwnerAddress/AddressField[1]", equalTo("abc123")).matches(xml));

        //RestrictedGoods
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/RestrictedGoods/GoodsExist", equalTo("true")).matches(xml));

        //Manifest
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/ManifestType", equalTo("GAR")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/CommonSegment/ManifestNo", equalTo("0")).matches(xml));

        //Aviation
        assertTrue(hasXPath("//eBorders_STTDocument/GenericAviation/AircraftRegistrationNumber", equalTo("TEST12345")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/GenericAviation/BasedAt", equalTo("EGAA")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/GenericAviation/AircraftType", equalTo("helicopter")).matches(xml));
        assertTrue(hasXPath("//eBorders_STTDocument/GenericAviation/FreeCirculationInEU", equalTo("true")).matches(xml));

    }

    @Test
    public void failedSubmissionTest() throws Exception {
        repo.deleteAll();

        UUID userUuid = UUID.randomUUID();
        UUID submissionUuid = UUID.randomUUID();

        //Create a pending record
        repo.saveAndFlush(SubmittedGarPersistedRecord.builder()
                .garUuid(UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71"))
                .status(SubmissionStatus.PENDING)
                .userUuid(userUuid)
                .submissionUuid(submissionUuid)
                .build());

        setMockCbpClientSuccess(false, false);

        String jsonRep = readFileAsString("files/FailedRequest.json");
        QueuedGarSubmissionToCbpPojo request = objectMapper.readValue(jsonRep, QueuedGarSubmissionToCbpPojo.class);
        //set submission id
        request.setSubmissionUuid(submissionUuid);

        //Se user values
        SubmittingUser user = SubmittingUserPojo.builder()
                .userUuid(userUuid)
                .email("")
                .build();
        request.setSubmittingUser(user);

        //WHEN
        underTest.processSubmission(objectMapper.writeValueAsString(request));

        //THEN
        List<SubmittedGarPersistedRecord> allRecords = repo.findAll();
        assertTrue(allRecords.size()==1);
        assertEquals(allRecords.get(0).getGarUuid(),(UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71")));
        assertEquals(allRecords.get(0).getUserUuid(),(userUuid));
        assertTrue(allRecords.get(0).getStatus()==SubmissionStatus.FAILED);
    }



    private void setMockCbpClientSuccess(boolean success, boolean exception){
        MockCbpClient clientMock = (MockCbpClient)cbpClient;
        clientMock.setSuccess(success);
        clientMock.setException(exception);
    }


    @Test
    public void cancelSubmissionSuccess() throws Exception {
        repo.deleteAll();
        setMockCbpClientSuccess(true,false);

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

        //Se user values
        SubmittingUser user = SubmittingUserPojo.builder()
                .userUuid(userUuid)
                .email("")
                .build();

        QueuedGarCancellationToCbpPojo request = QueuedGarCancellationToCbpPojo.builder()
                .submittingUser(user)
                .submissionUuid(submissionUuid)
                .externalSubmissionReference("REFERENCE")
                .build();
        //WHEN
        underTest.processCancellation(objectMapper.writeValueAsString(request));

        //THEN
        List<SubmittedGarPersistedRecord> allRecords = repo.findAll();
        assertTrue(allRecords.size()==1);
        assertEquals(allRecords.get(0).getGarUuid(),(UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71")));
        assertEquals(allRecords.get(0).getUserUuid(),(userUuid));
        assertTrue(allRecords.get(0).getStatus()==SubmissionStatus.CANCELLED);
    }

    @Test
    public void cancelSubmissionFailed() throws Exception {
        repo.deleteAll();
        setMockCbpClientSuccess(false, false);

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

        //Se user values
        SubmittingUser user = SubmittingUserPojo.builder()
                .userUuid(userUuid)
                .email("")
                .build();

        QueuedGarCancellationToCbpPojo request = QueuedGarCancellationToCbpPojo.builder()
                .submittingUser(user)
                .submissionUuid(submissionUuid)
                .externalSubmissionReference("REFERENCE")
                .build();
        //WHEN
        underTest.processCancellation(objectMapper.writeValueAsString(request));

        //THEN
        List<SubmittedGarPersistedRecord> allRecords = repo.findAll();
        assertTrue(allRecords.size()==1);
        assertEquals(allRecords.get(0).getGarUuid(),(UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71")));
        assertEquals(allRecords.get(0).getUserUuid(),(userUuid));
        assertTrue(allRecords.get(0).getStatus()==SubmissionStatus.SUBMITTED);
    }

    @Test
    public void cancelSubmissionException() throws Exception {
        repo.deleteAll();
        setMockCbpClientSuccess(false, true);

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

        //Se user values
        SubmittingUser user = SubmittingUserPojo.builder()
                .userUuid(userUuid)
                .email("")
                .build();

        QueuedGarCancellationToCbpPojo request = QueuedGarCancellationToCbpPojo.builder()
                .submittingUser(user)
                .submissionUuid(submissionUuid)
                .externalSubmissionReference("REFERENCE")
                .build();
        //WHEN
        underTest.processCancellation(objectMapper.writeValueAsString(request));

        //THEN
        List<SubmittedGarPersistedRecord> allRecords = repo.findAll();
        assertTrue(allRecords.size()==1);
        assertEquals(allRecords.get(0).getGarUuid(),(UUID.fromString("cfdcc109-d2d3-4c0c-812b-2b99dbe89d71")));
        assertEquals(allRecords.get(0).getUserUuid(),(userUuid));
        assertTrue(allRecords.get(0).getStatus()==SubmissionStatus.SUBMITTED);
    }

    @Test
    public void bugEgar1334Test() throws Exception {
        repo.deleteAll();

        UUID userUuid = UUID.randomUUID();
        UUID submissionUuid = UUID.randomUUID();

        //Create a pending record
        repo.saveAndFlush(SubmittedGarPersistedRecord.builder()
                .garUuid(UUID.fromString("87e895a6-283c-4c36-88d0-7bf993b8601b"))
                .status(SubmissionStatus.PENDING)
                .userUuid(userUuid)
                .submissionUuid(submissionUuid)
                .build());

        setMockCbpClientSuccess(true, false);


        //Using the successful request
        String jsonRep = readFileAsString("files/EGAR-1334_Request.json");
        QueuedGarSubmissionToCbpPojo request = objectMapper.readValue(jsonRep, QueuedGarSubmissionToCbpPojo.class);
        //set submission id
        request.setSubmissionUuid(submissionUuid);

//Se user values
        SubmittingUser user = SubmittingUserPojo.builder()
                .userUuid(userUuid)
                .email("")
                .build();
        request.setSubmittingUser(user);

        //WHEN
        underTest.processSubmission(objectMapper.writeValueAsString(request));

        //THEN
        List<SubmittedGarPersistedRecord> allRecords = repo.findAll();
        assertTrue(allRecords.size()==1);
        assertEquals(allRecords.get(0).getGarUuid(),(UUID.fromString("87e895a6-283c-4c36-88d0-7bf993b8601b")));
        assertEquals(allRecords.get(0).getUserUuid(),(userUuid));
        assertTrue(allRecords.get(0).getStatus()==SubmissionStatus.SUBMITTED);

    }

    private static Document parse(String xml) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            return documentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
