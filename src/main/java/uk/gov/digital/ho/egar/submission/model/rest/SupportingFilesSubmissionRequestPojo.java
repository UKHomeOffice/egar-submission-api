package uk.gov.digital.ho.egar.submission.model.rest;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.egar.submission.model.SupportingFilesSubmissionRequest;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupportingFilesSubmissionRequestPojo implements SupportingFilesSubmissionRequest {
	private UUID uuid;
	private String fileName;
 	private boolean uploadComplete;
	private String fileLink;
}
