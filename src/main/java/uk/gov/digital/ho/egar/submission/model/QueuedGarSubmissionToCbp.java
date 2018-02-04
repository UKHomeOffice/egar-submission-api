package uk.gov.digital.ho.egar.submission.model;

import java.util.UUID;

import uk.gov.digital.ho.egar.submission.model.jms.SubmittingUser;

public interface QueuedGarSubmissionToCbp extends GarSubmissionRequest {
	UUID getSubmissionUuid();
	SubmittingUser getSubmittingUser();
}
