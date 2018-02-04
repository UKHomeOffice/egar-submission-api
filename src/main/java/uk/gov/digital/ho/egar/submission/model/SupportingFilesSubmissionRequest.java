package uk.gov.digital.ho.egar.submission.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;
/**
 * @see SupportingFilesSubmissionRequestPojo 
 * @author Keshava.Grama
 *
 */
public interface SupportingFilesSubmissionRequest {
    @JsonProperty("file_uuid")
    public UUID getUuid();
    @JsonProperty("file_name")
    public String getFileName();
    @JsonProperty("upload_complete")
    public boolean isUploadComplete();
    @JsonProperty("file_link")
	public String getFileLink();
}
