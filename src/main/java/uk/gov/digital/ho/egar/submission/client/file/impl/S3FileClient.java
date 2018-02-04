package uk.gov.digital.ho.egar.submission.client.file.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.fincore.cbpcarrierwebservice.entities.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.submission.api.exceptions.FileNotFoundSubmissionApiException;
import uk.gov.digital.ho.egar.submission.client.file.FileClient;
import uk.gov.digital.ho.egar.submission.config.S3Config;
import uk.gov.digital.ho.egar.submission.model.SupportingFilesSubmissionRequest;

import java.io.IOException;
import java.io.InputStream;

@Component
@Profile({"!s3-mocks"})
public class S3FileClient implements FileClient {

	private final S3Config s3Config;
	
	private final AmazonS3 s3Client;
	
	public S3FileClient(@Autowired S3Config s3Config,@Autowired AmazonS3 s3Client) {
		super();
		this.s3Config = s3Config;
		this.s3Client = s3Client;
	}
	
	@Override
	public File getFile(SupportingFilesSubmissionRequest supportingFile) throws IOException, FileNotFoundSubmissionApiException {
		
		String filename = constructFilename(supportingFile);

		// Retrieves file from path
		S3Object object = s3Client.getObject(new GetObjectRequest(s3Config.getBucket(), filename));
		
		if(object == null) throw new FileNotFoundSubmissionApiException("File does not exist");
		InputStream objectData = object.getObjectContent();

		// convert inputStream in to bytes
		byte[] bytes = IOUtils.toByteArray(objectData);

		File file = new File();
		file.setData(bytes);
		file.setFilename(supportingFile.getFileName());
		
		return file;
	}

	/**
	 * Constructs the filename by combining the uuid and original filename
	 *
	 * @param details
	 *
	 * @return The new filename.
	 */
	private String constructFilename(SupportingFilesSubmissionRequest details) {
		return details.getUuid().toString() + "/" + details.getFileName();
	}
	
	

}
