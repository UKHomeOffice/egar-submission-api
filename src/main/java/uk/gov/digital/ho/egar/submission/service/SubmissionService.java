package uk.gov.digital.ho.egar.submission.service;

import java.util.List;
import java.util.UUID;

import uk.gov.digital.ho.egar.shared.auth.api.token.UserValues;
import uk.gov.digital.ho.egar.submission.api.exceptions.DataNotFoundSubmissionApiException;
import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionApiException;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionRequest;
import uk.gov.digital.ho.egar.submission.model.SubmittedGar;

public interface SubmissionService {

	UUID submitGar(GarSubmissionRequest submissionRequest, UserValues userValues) throws SubmissionApiException;

	SubmittedGar getSubmittedGar(UUID uuidOfUser, UUID submissionUuid) throws DataNotFoundSubmissionApiException;

    UUID cancelSubmittedGar(UUID submissionUuid, final UserValues userValues) throws SubmissionApiException;

	SubmittedGar[] getBulkSubmissions(final UUID uuidOfUser, final List<UUID> submissionUuids);
}
