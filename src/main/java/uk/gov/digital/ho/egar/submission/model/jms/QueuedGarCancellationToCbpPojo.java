package uk.gov.digital.ho.egar.submission.model.jms;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Builder;
import lombok.Data;
import uk.gov.digital.ho.egar.submission.model.QueuedGarCancellationToCbp;

@Data
@Builder
public class QueuedGarCancellationToCbpPojo implements QueuedGarCancellationToCbp {
	@JsonProperty("external_reference")
	@NotNull
	private String externalSubmissionReference;
	
	@JsonProperty("submission_uuid")
	@NotNull
	private UUID submissionUuid;
	
	@JsonProperty("submitting_user")
	@JsonDeserialize(as=SubmittingUserPojo.class)
	private SubmittingUser submittingUser;
}
