package uk.gov.digital.ho.egar.submission.client.file.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.fincore.cbpcarrierwebservice.entities.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.submission.api.exceptions.FileNotFoundSubmissionApiException;
import uk.gov.digital.ho.egar.submission.client.file.FileClient;
import uk.gov.digital.ho.egar.submission.config.S3Config;
import uk.gov.digital.ho.egar.submission.model.SupportingFilesSubmissionRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Component
@Profile({"!s3-mocks"})
public class S3FileClient implements FileClient {
	Log logger = LogFactory.getLog(S3FileClient.class);
	
	private final S3Config s3Config;
	
	private final AmazonS3 s3Client;
	
	public S3FileClient(@Autowired S3Config s3Config,@Autowired AmazonS3 s3Client) {
		super();
		this.s3Config = s3Config;
		this.s3Client = s3Client;
	}
	
	@Override
	public File getFile(SupportingFilesSubmissionRequest supportingFile) throws IOException, FileNotFoundSubmissionApiException {
		
		AmazonS3URI uri = new AmazonS3URI(supportingFile.getFileLink());

		String key = urlDecodeKey(uri);

		logger.info("Attempting to retrieve file " + key + " from  bucket " + uri.getBucket() + " on s3");

		// Retrieves file from path
		S3Object object = s3Client.getObject(new GetObjectRequest(uri.getBucket(), key));
		
		if(object == null) throw new FileNotFoundSubmissionApiException("File does not exist");
		InputStream objectData = object.getObjectContent();

		// convert inputStream in to bytes
		byte[] bytes = IOUtils.toByteArray(objectData);

		File file = new File();
		file.setData(bytes);
		file.setFilename(supportingFile.getFileName());
		
		return file;
	}

	public String urlDecodeKey(AmazonS3URI uri){
		String key = uri.getKey();
		return urlDecodeValue(key);
	}

	private String urlDecodeValue(String key){
		String urlDecodedKey = key;
		try {
			urlDecodedKey = URLDecoder.decode(urlDecodedKey, UTF_8);
		} catch (UnsupportedEncodingException e) {
			logger.error(String.format("Unable to decode '%s' with format '%s'", urlDecodedKey, UTF_8), e);
		}
		return urlDecodedKey;
	}

}
