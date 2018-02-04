package uk.gov.digital.ho.egar.submission.model.rest;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import uk.gov.digital.ho.egar.submission.model.SupportingFilesSubmissionRequest;

@Data
@Builder
public class SupportingFilesSubmissionRequestPojo implements SupportingFilesSubmissionRequest {
	private UUID uuid;
	private String fileName;
 	private boolean uploadComplete;
	private String fileLink;
}
