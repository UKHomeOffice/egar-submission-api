package uk.gov.digital.ho.egar.submission.model.jms;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Builder;
import lombok.Data;
import uk.gov.digital.ho.egar.submission.model.Aircraft;
import uk.gov.digital.ho.egar.submission.model.GeographicLocation;
import uk.gov.digital.ho.egar.submission.model.PersonsSubmissionRequest;
import uk.gov.digital.ho.egar.submission.model.QueuedGarSubmissionToCbp;
import uk.gov.digital.ho.egar.submission.model.SupportingFilesSubmissionRequest;
import uk.gov.digital.ho.egar.submission.model.rest.AircraftRestPojo;
import uk.gov.digital.ho.egar.submission.model.rest.Attribute;
import uk.gov.digital.ho.egar.submission.model.rest.GeographicLocationPojo;
import uk.gov.digital.ho.egar.submission.model.rest.PersonsSubmissionRequestPojo;
import uk.gov.digital.ho.egar.submission.model.rest.SupportingFilesSubmissionRequestPojo;

@Data
@Builder
public class QueuedGarSubmissionToCbpPojo implements QueuedGarSubmissionToCbp {
	@JsonProperty("gar_uuid")
	@NotNull
	private UUID garUuid;

	@JsonDeserialize(as=AircraftRestPojo.class)
	private Aircraft aircraft;
	
	@JsonProperty("location")
	@JsonDeserialize(contentAs= GeographicLocationPojo.class)
	private List<GeographicLocation> locations;
	
	@JsonProperty("people")
	@JsonDeserialize(as= PersonsSubmissionRequestPojo.class)
	private PersonsSubmissionRequest persons;
	
	@JsonProperty("files")
	@JsonDeserialize(contentAs=SupportingFilesSubmissionRequestPojo.class)
	private List<SupportingFilesSubmissionRequest> supportingFiles;
	
	@JsonProperty("attributes")
	private Attribute attributes;
	
	@JsonProperty("submission_uuid")
	private UUID submissionUuid;
	
	@JsonProperty("submitting_user")
	@JsonDeserialize(as=SubmittingUserPojo.class)
	private SubmittingUser submittingUser;
}
