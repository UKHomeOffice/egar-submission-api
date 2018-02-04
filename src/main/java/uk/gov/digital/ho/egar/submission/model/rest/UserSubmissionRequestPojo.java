package uk.gov.digital.ho.egar.submission.model.rest;

import lombok.Data;
import uk.gov.digital.ho.egar.submission.model.UserSubmissionRequest;

@Data
public class UserSubmissionRequestPojo implements UserSubmissionRequest {

	private String email;
	private String forename;
	private String surname;
	private String contactNumber;
	private String alternativeContactNum;
}
