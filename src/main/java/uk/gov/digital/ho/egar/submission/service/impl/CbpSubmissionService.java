package uk.gov.digital.ho.egar.submission.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.shared.auth.api.token.UserValues;
import uk.gov.digital.ho.egar.submission.api.RestConstants;
import uk.gov.digital.ho.egar.submission.api.exceptions.BadOperationSubmissionApiException;
import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionApiException;
import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionNotFoundSubmissionApiException;
import uk.gov.digital.ho.egar.submission.client.jms.SubmissionExecutionService;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionRequest;
import uk.gov.digital.ho.egar.submission.model.QueuedGarSubmissionToCbp;
import uk.gov.digital.ho.egar.submission.model.SubmissionStatus;
import uk.gov.digital.ho.egar.submission.model.jms.QueuedGarCancellationToCbpPojo;
import uk.gov.digital.ho.egar.submission.model.jms.QueuedGarSubmissionToCbpPojo;
import uk.gov.digital.ho.egar.submission.model.jms.SubmittingUserPojo;
import uk.gov.digital.ho.egar.submission.service.SubmissionService;
import uk.gov.digital.ho.egar.submission.service.repository.SubmittedGarPersistedRecordRepository;
import uk.gov.digital.ho.egar.submission.service.repository.model.SubmittedGarPersistedRecord;

/**
 * Service object to submit the summary gar representation to CBP
 * @author Keshava.Grama
 *
 */
@Service
public class CbpSubmissionService implements SubmissionService {
	private static Log logger = LogFactory.getLog(CbpSubmissionService.class);

	@Autowired
	private SubmittedGarPersistedRecordRepository repository;

	@Autowired
	private SubmissionExecutionService executor;


	public UUID submitGar(final GarSubmissionRequest submissionRequest, final UserValues userValues) throws SubmissionApiException {
		logger.debug(String.format("Submitting gar '%s' request to CBP for user '%s'", submissionRequest.getGarUuid(), userValues.getUserUuid()));

		//Check if the request has been previously submitted
		checkForPreviousSubmission(submissionRequest.getGarUuid(), userValues.getUserUuid());

		//Persist request
		UUID submissionUuid = persistSubmission(submissionRequest, userValues);

		//Build queue request
		QueuedGarSubmissionToCbp queuedSubmission = QueuedGarSubmissionToCbpPojo.builder()
				.submissionUuid(submissionUuid)
				.submittingUser(SubmittingUserPojo.builder()
				.userUuid(userValues.getUserUuid())
				.email(userValues.getEmail())
				.build())
				.aircraft(submissionRequest.getAircraft())
				.attributes(submissionRequest.getAttributes())
				.garUuid(submissionRequest.getGarUuid())
				.locations(submissionRequest.getLocations())
				.persons(submissionRequest.getPersons())
				.supportingFiles(submissionRequest.getSupportingFiles())
				.build();

		// Queue submission request
		executor.processSubmission(queuedSubmission);

		return submissionUuid;
	}

	public SubmittedGarPersistedRecord getSubmittedGar(final UUID userUuid, final UUID submissionUuid) throws SubmissionNotFoundSubmissionApiException {
		SubmittedGarPersistedRecord submittedGar = repository.findOneBySubmissionUuidAndUserUuid(submissionUuid, userUuid);

		if (submittedGar==null){
			throw new SubmissionNotFoundSubmissionApiException(submissionUuid,userUuid);
		}

		return submittedGar;
	}

	@Override
	public UUID cancelSubmittedGar(UUID submissionUuid, final UserValues userValues) throws SubmissionApiException {
		SubmittedGarPersistedRecord submittedGar = getSubmittedGar(userValues.getUserUuid(), submissionUuid);

		if (submittedGar.getStatus()!=SubmissionStatus.SUBMITTED){
			throw new BadOperationSubmissionApiException(String.format("Submission '%s' for user '%s' is not in a valid state to cancel", submissionUuid,userValues.getUserUuid()));
		}

		//Sets the gar to a pending state whilst the cancellation operation is carried out.
		submittedGar.setStatus(SubmissionStatus.PENDING);
		repository.saveAndFlush(submittedGar);

		QueuedGarCancellationToCbpPojo queuedCancellation = QueuedGarCancellationToCbpPojo.builder()
				.externalSubmissionReference(submittedGar.getExternalSubmissionRef())
				.submissionUuid(submissionUuid)
				.submittingUser(SubmittingUserPojo.builder()
						.userUuid(userValues.getUserUuid())
						.email(userValues.getEmail())
						.build())
				.build();

		//Calling cbp cancellation asynchronously
		executor.processCancellation(queuedCancellation);

		return submittedGar.getSubmissionUuid();
	}

	

	private UUID persistSubmission(final GarSubmissionRequest submissionRequest, final AuthValues authValues){
		SubmittedGarPersistedRecord record = SubmittedGarPersistedRecord.builder()
				.submissionType(RestConstants.SUBMISSION_TYPE)
				.userUuid(authValues.getUserUuid())
				.garUuid(submissionRequest.getGarUuid())
				.submissionUuid(UUID.randomUUID())
				.status(SubmissionStatus.PENDING)
				.build();

		record = record.updateEditDateTime();

		repository.saveAndFlush(record);

		return record.getSubmissionUuid();
	}

	private void checkForPreviousSubmission(UUID garUuid, UUID userUuid) throws BadOperationSubmissionApiException {
		List<SubmittedGarPersistedRecord> repositoryObj = 
				repository.findByGarUuidAndUserUuidAndStatusIsIn(garUuid, userUuid, Arrays.asList(SubmissionStatus.SUBMITTED, SubmissionStatus.PENDING));
		if (repositoryObj!= null && repositoryObj.size() > 0) {
			throw new BadOperationSubmissionApiException(String.format("Submission already exists for GAR '%s' and user '%s'", garUuid, userUuid));
		}
	}

}
