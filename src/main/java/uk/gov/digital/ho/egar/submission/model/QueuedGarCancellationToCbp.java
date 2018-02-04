package uk.gov.digital.ho.egar.submission.model;

import uk.gov.digital.ho.egar.submission.model.jms.SubmittingUser;

import java.util.UUID;

public interface QueuedGarCancellationToCbp {
	UUID getSubmissionUuid();
	String getExternalSubmissionReference();
	SubmittingUser getSubmittingUser();
}
