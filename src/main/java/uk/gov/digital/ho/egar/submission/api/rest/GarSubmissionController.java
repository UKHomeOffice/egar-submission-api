package uk.gov.digital.ho.egar.submission.api.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.shared.auth.api.token.UserValues;
import uk.gov.digital.ho.egar.submission.api.GarSubmission;
import uk.gov.digital.ho.egar.submission.api.RestConstants;
import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionApiException;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionResponse;
import uk.gov.digital.ho.egar.submission.model.SubmittedGar;
import uk.gov.digital.ho.egar.submission.model.rest.GarSubmissionRequestPojo;
import uk.gov.digital.ho.egar.submission.model.rest.GarSubmissionResponsePojo;
import uk.gov.digital.ho.egar.submission.service.SubmissionService;
import uk.gov.digital.ho.egar.submission.utils.UriLocationUtilities;

@RestController
@RequestMapping(RestConstants.ROOT_PATH)
@Api(value = RestConstants.ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class GarSubmissionController implements GarSubmission {

    protected final Log logger = LogFactory.getLog(GarSubmissionController.class);

    private SubmissionService submissionService;

    private UriLocationUtilities uriLocationUtilities;

    public GarSubmissionController(@Autowired final SubmissionService submissionService,
                                   @Autowired final UriLocationUtilities uriLocationUtilities) {
        this.uriLocationUtilities = uriLocationUtilities;
        this.submissionService = submissionService;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Submit a gar.",
            notes = "Submit a gar to CBP")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 303,
                    message = "see other"),
            @ApiResponse(
                    code = 400,
                    message = "bad request"),
            @ApiResponse(
                    code = 401,
                    message = "unauthorised"),
            @ApiResponse(
                    code = 403,
                    message = "forbidden")})
    @PostMapping(value = RestConstants.PATH_SUBMIT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.SEE_OTHER)
    @Override
    public ResponseEntity<Void> submit(@RequestHeader(value = UserValues.AUTH_HEADER, required = false) String authHeader,
                                       @RequestHeader(value = UserValues.USERID_HEADER, required = true) UUID uuidOfUser,
                                       @RequestHeader(value = UserValues.FORENAME_HEADER, required = false) String forename,
                                       @RequestHeader(value = UserValues.SURNAME_HEADER, required = false) String surname,
                                       @RequestHeader(value = UserValues.EMAIL_HEADER, required = true) String email,
                                       @RequestHeader(value = UserValues.CONTACT_HEADER, required = false) String contact,
                                       @RequestHeader(value = UserValues.ALTERNATIVE_CONTACT_HEADER, required= false) String altContact,
                                       @RequestBody @Valid GarSubmissionRequestPojo request,
                                       Errors errors) throws SubmissionApiException {

        if (errors.hasErrors()){
            return new ResponseEntity(new ApiErrors(errors), HttpStatus.BAD_REQUEST);
        }

        UserValues userValues = new UserValues(authHeader,uuidOfUser, email, forename, surname, contact, altContact);

        UUID submissionUuid = submissionService.submitGar(request, userValues);

        //Creating the redirection location URI
        URI redirectLocation = uriLocationUtilities.getSubmissionUri(submissionUuid);

        logger.debug("Redirection url for submission is: " + redirectLocation);

        //Creating the response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(redirectLocation);

        return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);
    }

    @ApiOperation(value = "Retrieve submission details for a gar.",
            notes = "Retrieve submission details for a gar from CBP")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 303,
                    message = "see other"),
            @ApiResponse(
                    code = 400,
                    message = "bad request"),
            @ApiResponse(
                    code = 401,
                    message = "unauthorised"),
            @ApiResponse(
                    code = 403,
                    message = "forbidden")})
    @GetMapping(value = RestConstants.PATH_PREVIOUS_SUBMISSION_PATH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GarSubmissionResponse retrieve( @RequestHeader(UserValues.USERID_HEADER) UUID uuidOfUser,
                                           @PathVariable(RestConstants.PATH_PREVIOUS_SUBMISSION) UUID submissionUuid) 
                                        		   throws SubmissionApiException {
        SubmittedGar persistedSubmission = submissionService.getSubmittedGar(uuidOfUser, submissionUuid);

        return createGarSubmissionResponse(persistedSubmission);
    }

    @ApiOperation(value = "Cancel the submission of a submitted GAR.",
            notes = "Cancels submission of a submitted gar from CBP")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 303,
                    message = "see other"),
            @ApiResponse(
                    code = 400,
                    message = "bad request"),
            @ApiResponse(
                    code = 401,
                    message = "unauthorised"),
            @ApiResponse(
                    code = 403,
                    message = "forbidden")})
    @DeleteMapping(value = RestConstants.PATH_PREVIOUS_SUBMISSION_PATH)
    public ResponseEntity<Void> cancelSubmission( @RequestHeader(value = UserValues.AUTH_HEADER, required = false) String authHeader,
                                                  @RequestHeader(value = UserValues.USERID_HEADER, required = true) UUID uuidOfUser,
                                                  @RequestHeader(value = UserValues.FORENAME_HEADER, required = false) String forename,
                                                  @RequestHeader(value = UserValues.SURNAME_HEADER, required = false) String surname,
                                                  @RequestHeader(value = UserValues.EMAIL_HEADER, required = true) String email,
                                                  @RequestHeader(value = UserValues.CONTACT_HEADER, required = false) String contact,
                                                  @RequestHeader(value = UserValues.ALTERNATIVE_CONTACT_HEADER, required= false) String altContact,
                                                  @PathVariable(RestConstants.PATH_PREVIOUS_SUBMISSION) UUID submissionUuid)
            throws SubmissionApiException {

        UserValues userValues = new UserValues(authHeader,uuidOfUser, email, forename, surname, contact, altContact);

        UUID persistedSubmissionUuuid = submissionService.cancelSubmittedGar(submissionUuid, userValues);

        //Creating the redirection location URI
        URI redirectLocation = uriLocationUtilities.getSubmissionUri(persistedSubmissionUuuid);

        logger.debug("Redirection url for submission is: " + redirectLocation);

        //Creating the response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(redirectLocation);

        return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);

    }
    
    //---------------------------------------------------------------------------------------------------------

    /**
     * A get endpoint that bulk retrieves a list of Summary details
     */


    @Override
    @ApiOperation(value = "Bulk retrieve a list of Summary details.",
    notes = "Retrieve a list of existing Summary details for a user from CBP")
    @ApiResponses(value = {
    		@ApiResponse(
    				code = 200,
    				message = "Succesful retrieval",
    				response = GarSubmissionResponse[].class),
    		@ApiResponse(
    				code = 401,
    				message = "Unauthrised")})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = RestConstants.PATH_BULK,
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public GarSubmissionResponse[] bulkRetrieveGARs(@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser, 
    												@RequestBody List<UUID> submissionUuids) {

    	SubmittedGar[] persistedSubmissions =submissionService.getBulkSubmissions(uuidOfUser,submissionUuids);
    	
    	List<GarSubmissionResponse> response = Arrays.asList(persistedSubmissions)
    										   .stream()
    										   .map(skel -> createGarSubmissionResponse(skel))
    										   .collect(Collectors.toList());   
    	
    	return response.toArray(new GarSubmissionResponse[persistedSubmissions.length]);
    }
    

    private GarSubmissionResponse createGarSubmissionResponse(SubmittedGar submittedGar) {
        GarSubmissionResponsePojo response = new GarSubmissionResponsePojo();
        response.setSubmission(submittedGar);
        response.setGarUuid(submittedGar.getGarUuid());
        response.setUserUuid(submittedGar.getUserUuid());
        return response;
    }

    public static class ApiErrors {

        @JsonProperty("message")
        private final List<String> errorMessages = new ArrayList<>();

        public ApiErrors(Errors errors) {
            for(final FieldError error : errors.getFieldErrors()){
                errorMessages.add(error.getField() + ": " + error.getDefaultMessage());
            }
            for(final ObjectError error : errors.getGlobalErrors()){
                errorMessages.add(error.getObjectName() + ": " + error.getDefaultMessage());
            }
        }
    }

}
