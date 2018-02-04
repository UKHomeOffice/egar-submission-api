package uk.gov.digital.ho.egar.submission.service.impl;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fincore.cbpcarrierwebservice.entities.ArrayOfFile;

import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionApiException;
import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionTransformationException;
import uk.gov.digital.ho.egar.submission.client.cbp.CbpClient;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.GarFileMarshaller;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.GarXmlMarshaller;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionRequest;
import uk.gov.digital.ho.egar.submission.model.QueuedGarCancellationToCbp;
import uk.gov.digital.ho.egar.submission.model.QueuedGarSubmissionToCbp;
import uk.gov.digital.ho.egar.submission.model.SubmissionStatus;
import uk.gov.digital.ho.egar.submission.model.jms.SubmittingUser;
import uk.gov.digital.ho.egar.submission.model.service.CbpSubmissionResponse;
import uk.gov.digital.ho.egar.submission.service.CbpSubmissionProcessor;
import uk.gov.digital.ho.egar.submission.service.repository.SubmittedGarPersistedRecordRepository;
import uk.gov.digital.ho.egar.submission.service.repository.model.SubmittedGarPersistedRecord;

@Component
public class CbpSubmissionProcessorImpl implements CbpSubmissionProcessor {
	private static Log logger = LogFactory.getLog(CbpSubmissionProcessorImpl.class);

	private static String MANIFEST_TYPE = "GAR";

	@Autowired
	private GarXmlMarshaller xmlGenerator;

	@Autowired
	private GarFileMarshaller excelGarMarshaller;
	
	@Autowired
	private CbpClient cbpClient;

	@Autowired
	private SubmittedGarPersistedRecordRepository repository;

	@Override
	public void processSubmissionRequests(QueuedGarSubmissionToCbp submission) throws IOException {
		if (submission.getSupportingFiles() == null || submission.getSupportingFiles().size() == 0) {
			try {
				String submissionXml = convertRequestToXmlString(submission, submission.getSubmittingUser());
				submitToCbpAndPersistResponse(submission.getSubmittingUser(), submissionXml, submission.getSubmissionUuid());
			} catch (SubmissionTransformationException e) {
				logger.error(String.format("Submission '%s' failed to submit to CBP", submission.getSubmissionUuid()), e);
				failSubmission(submission.getSubmittingUser(), submission.getSubmissionUuid());
			}
		}else {
			try {
				ArrayOfFile arrayOfFile = excelGarMarshaller.convertPojoToExcel(submission);
				submitToCbpAndPersistResponse(submission.getSubmittingUser(), arrayOfFile, submission.getSubmissionUuid());
			} catch (SubmissionApiException|InvalidFormatException e) {
				logger.error(String.format("Submission '%s' failed to submit to CBP", submission.getSubmissionUuid()), e);
				failSubmission(submission.getSubmittingUser(), submission.getSubmissionUuid());
			}
		}
	}

	@Override
	public void processCancellationRequest(QueuedGarCancellationToCbp cancellation) {
		SubmittedGarPersistedRecord submittedGar = repository.findOneBySubmissionUuidAndUserUuid(cancellation.getSubmissionUuid(), cancellation.getSubmittingUser().getUserUuid());
		try {
			//Submit cancel to CBP
			CbpSubmissionResponse response = cbpClient.cancelSubmittedGar(cancellation.getExternalSubmissionReference());

			if (!response.isSuccess()){
				logger.info(String.format("Cancellation submission failed for gar '%s' responded with '%s", submittedGar.getGarUuid(), response.getReason()));
			}

			submittedGar.setStatus(response.isSuccess()? SubmissionStatus.CANCELLED: SubmissionStatus.SUBMITTED);

			repository.saveAndFlush(submittedGar);
		}
		catch (Exception e){
			logger.error(String.format("Submission '%s' failed to cancel to CBP", submittedGar.getSubmissionUuid()), e);
			submittedGar.setStatus(SubmissionStatus.SUBMITTED);
			repository.saveAndFlush(submittedGar);
		}
	}

	/**
	 * Submit an Array of files to CBP
	 * @param submittingUser
	 * @param arrayOfFile
	 * @param submissionUuid
	 */
	private void submitToCbpAndPersistResponse(SubmittingUser submittingUser, ArrayOfFile arrayOfFile,
											   UUID submissionUuid) {
		try {
			//Submit to CBP
			CbpSubmissionResponse cbpResponse = cbpClient.submitWithAttachments(arrayOfFile);

			logger.debug("Reference returned " + cbpResponse.getIdentifier());
			updateSubmissionWithResponse(submittingUser, submissionUuid, cbpResponse);
		}
		catch (Exception e){
			logger.error(String.format("Submission '%s' failed to submit to CBP", submissionUuid), e);
			failSubmission(submittingUser, submissionUuid);
		}
	}

	/**
	 * Converts the request to an xml string
	 * @param garSubmissionRequest The submission request
	 * @return The xml string
	 */
	private String convertRequestToXmlString(final GarSubmissionRequest garSubmissionRequest,final SubmittingUser userValues) throws SubmissionTransformationException {
		try {
			return xmlGenerator.marshalGarSubmissionRequest(garSubmissionRequest,userValues,  MANIFEST_TYPE, ZonedDateTime.now());
		} catch (Exception e) {
			logger.error("Unable to transform request to xml.",e);
			throw new SubmissionTransformationException(garSubmissionRequest.getGarUuid(), userValues.getUserUuid(), e);
		}
	}

	/**
	 * Submits to cbp and stores the response against the submission uuid
	 * @param submittingUser The user authentication values
	 * @param cbpXml The xml to send cbp
	 * @param submissionUuid The submission uuid
	 */
	private void submitToCbpAndPersistResponse(SubmittingUser submittingUser, String cbpXml, UUID submissionUuid){
		try {
			//Submit to CBP
			CbpSubmissionResponse cbpResponse = cbpClient.submitSTTXML(cbpXml);

			logger.debug("Reference returned " + cbpResponse.getIdentifier());
			updateSubmissionWithResponse(submittingUser, submissionUuid, cbpResponse);
		}
		catch (Exception e){
			logger.error(String.format("Submission '%s' failed to submit to CBP", submissionUuid), e);
			failSubmission(submittingUser, submissionUuid);
		}
	}

	/**
	 * Updates the submission with the submission response object
	 * @param authValues The authenication values for the user.
	 * @param submissionUuid The submission uuid.
	 * @param submissionResponse The submission response from CBP
	 * @return The submission uuid.
	 */
	private UUID updateSubmissionWithResponse(final SubmittingUser authValues, final UUID submissionUuid, final CbpSubmissionResponse submissionResponse) {

		SubmittedGarPersistedRecord record = repository.findOneBySubmissionUuidAndUserUuid(submissionUuid, authValues.getUserUuid());

		record.setExternalSubmissionReason(truncateReason(submissionResponse));
		record.setExternalSubmissionRef(submissionResponse.getIdentifier());
		record.setStatus(submissionResponse.isSuccess()?SubmissionStatus.SUBMITTED: SubmissionStatus.FAILED);

		record = record.updateEditDateTime();

		repository.saveAndFlush(record);

		return record.getSubmissionUuid();
	}

	/**
	 * Update the submission with a failed status
	 * @param submittingUser
	 * @param submissionUuid
	 * @return the submission UUID.
	 */
	private UUID failSubmission(final SubmittingUser submittingUser, final UUID submissionUuid) {

		SubmittedGarPersistedRecord record = repository.findOneBySubmissionUuidAndUserUuid(submissionUuid, submittingUser.getUserUuid());
		record.setStatus(SubmissionStatus.FAILED);

		record = record.updateEditDateTime();

		repository.saveAndFlush(record);

		return record.getSubmissionUuid();
	}

	/**
	 * Truncates the submission reason to 500 characters max
	 * @param submissionResponse The submission response
	 * @return The truncated reason.
	 */
	private String truncateReason(CbpSubmissionResponse submissionResponse) {
		String submissionReason = submissionResponse.getReason();
		if (submissionReason!=null){
			submissionReason = submissionReason.substring(0, Math.min(submissionReason.length(), 500));
		}
		return submissionReason;
	}
}
