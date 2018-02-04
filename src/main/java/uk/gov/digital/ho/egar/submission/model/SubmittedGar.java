package uk.gov.digital.ho.egar.submission.model;

import java.util.UUID;

/**
 * TODO implement this (potentially admin func)
 * @author Keshava.Grama
 *
 */
public interface SubmittedGar {
	public UUID getSubmissionUuid();
	public String getSubmissionType();
	public String getExternalSubmissionRef();
	public UUID getUserUuid();
	public UUID getGarUuid();
	public String getExternalSubmissionReason();
	public SubmissionStatus getStatus();
}
