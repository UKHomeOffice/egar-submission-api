package uk.gov.digital.ho.egar.submission.model.rest;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionResponse;
import uk.gov.digital.ho.egar.submission.model.SubmittedGar;

@Data
public class GarSubmissionResponsePojo implements GarSubmissionResponse {
	
	@JsonProperty("gar_uuid")
	private UUID garUuid;
	
	@JsonProperty("user_uuid")
	private UUID userUuid;

	private SubmittedGar submission;

}
