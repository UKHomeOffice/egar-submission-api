package uk.gov.digital.ho.egar.submission.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import org.springframework.validation.Errors;
import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionApiException;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionResponse;
import uk.gov.digital.ho.egar.submission.model.rest.GarSubmissionRequestPojo;

public interface GarSubmission  {
    public ResponseEntity<Void> submit(String authHeader,
                                       UUID uuidOfUser,
                                       String forename,
                                       String surname,
                                       String email,
                                       String contact,
                                       String altContact,
                                       GarSubmissionRequestPojo request,
                                       Errors errors) throws SubmissionApiException;

    public GarSubmissionResponse retrieve(UUID userUuid, UUID uuid) throws SubmissionApiException;
}
