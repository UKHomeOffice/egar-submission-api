package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import java.time.ZonedDateTime;

import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionApiException;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionRequest;
import uk.gov.digital.ho.egar.submission.model.jms.SubmittingUser;

public interface GarXmlMarshaller {

	String marshalGarSubmissionRequest(GarSubmissionRequest garSnapshot, SubmittingUser userValues, String manifestType, ZonedDateTime manifestTime)
			throws SubmissionApiException;

}