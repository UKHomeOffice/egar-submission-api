package uk.gov.digital.ho.egar.submission.client.file;

import java.io.IOException;

import com.fincore.cbpcarrierwebservice.entities.File;

import uk.gov.digital.ho.egar.submission.api.exceptions.FileNotFoundSubmissionApiException;
import uk.gov.digital.ho.egar.submission.api.exceptions.UnsupportedClientException;
import uk.gov.digital.ho.egar.submission.model.SupportingFilesSubmissionRequest;

public interface FileClient {
	 public static final String UTF_8 = "UTF-8";
	/**
	 * Retrieve file from client
	 * @param supportingFile
	 * @return File object if found, exception thrown on error, null if no object.
	 * @throws UnsupportedClientException
	 * @throws IOException
	 */
	public File getFile(SupportingFilesSubmissionRequest supportingFile) throws IOException, FileNotFoundSubmissionApiException;

}
