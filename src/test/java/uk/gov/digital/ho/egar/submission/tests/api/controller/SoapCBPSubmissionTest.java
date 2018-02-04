package uk.gov.digital.ho.egar.submission.tests.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.digital.ho.egar.submission.tests.utils.FileReaderUtils.readFileAsString;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule;
import uk.co.civica.microservice.util.testing.utils.ConditionalIgnoreRule.ConditionalIgnore;
import uk.co.civica.microservice.util.testing.utils.IgnoreWhenNoSoapClients;
import uk.gov.digital.ho.egar.submission.SubmissionApplication;
import uk.gov.digital.ho.egar.submission.api.RestConstants;
import uk.gov.digital.ho.egar.submission.client.cbp.CbpClient;
import uk.gov.digital.ho.egar.submission.model.SubmissionStatus;
import uk.gov.digital.ho.egar.submission.service.repository.SubmittedGarPersistedRecordRepository;
import uk.gov.digital.ho.egar.submission.service.repository.model.SubmittedGarPersistedRecord;

/**
 * This tests in this suite are run in name order in the following order
 * <b> You need an external mock of the CBP stub </b>
 * b. Verify that the mock SOAP service is deployed and reachable
 * c. Verify that the application has been instantiated correctly
 * d. Verify that the heartbeat service is reachable
 * e. Send a request to the mock CBP service and get a response i.e the SOAP connectivity code is working.
 *
 * @author Keshava.Grama
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties
        = {
        "cbp.username=false",
        "cbp.password=false",
        "cbp.url=http://localhost:9046/CBP/CarrierWebService/CarrierService.svc",
        "spring.jackson.serialization.write_dates_as_timestamps: false",
        "cbp.cancellation.enabled=false",
        "spring.profiles.active=sync,s3-mocks,jms-mocks"
})
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ConditionalIgnore( condition = IgnoreWhenNoSoapClients.class )
public class SoapCBPSubmissionTest {

    @Rule
    public ConditionalIgnoreRule rule = new ConditionalIgnoreRule();

    private static final String EMAIL_HEADER = "x-auth-email";
    private static final String FORENAME_HEADER = "x-auth-given-name";
    private static final String SURNAME_HEADER = "x-auth-family-name";
    private static final String CONTACT_HEADER = "x-auth-contact-number";
    private static final String ALTERNATIVE_CONTACT_HEADER = "x-auth-alt-contact-number";
    private static final String USERID_HEADER = "x-auth-subject";
    private static final String AUTH_HEADER = "Authorization";

    @Autowired
    private SubmittedGarPersistedRecordRepository repo;

    @Autowired
    private SubmissionApplication app;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CbpClient cbpClient;

    @MockBean
    private AmazonS3 amazonS3;

    @MockBean
    private JmsTemplate jmsSender;


    private static String mockCBPSoapPath = "http://localhost:9046/CBP/CarrierWebService/CarrierService.svc";


    @Test
    public void bMockCBPServiceIsRunning() throws IOException {
        URL u = new URL(mockCBPSoapPath);
        HttpURLConnection huc = (HttpURLConnection) u.openConnection();
        huc.setRequestMethod("GET");
        huc.connect();
        System.out.println("Response from SOAP UI: " + huc.getResponseCode());
        huc.disconnect();
    }

    @Test
    public void cApplicationLoaded() {
        assertThat(app).isNotNull();
    }

    @Test
    public void dMockStubHeartbeat() {
        assertNotNull("Client is null", cbpClient);
        assertTrue("Mock service endpoint responded with false!", cbpClient.isCBPServiceAvailable());
    }

    @Test
    public void eSuccessfulSubmissionTest() throws Exception {
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
        assertEquals(allRecords.get(0).getUserUuid(), (userUuid));
        System.out.println("Test submission status: " + allRecords.get(0).getStatus());
        assertTrue(allRecords.get(0).getStatus() == SubmissionStatus.SUBMITTED);
    }

}
