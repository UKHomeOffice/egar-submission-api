package uk.gov.digital.ho.egar.submission.model;

import java.util.UUID;

public interface GarSubmissionResponse {
	public UUID getGarUuid();
	public UUID getUserUuid();
	public SubmittedGar getSubmission();
}
