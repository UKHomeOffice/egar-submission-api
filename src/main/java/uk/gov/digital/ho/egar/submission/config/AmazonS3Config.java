package uk.gov.digital.ho.egar.submission.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Setting up the AmazonS3 client.
 */
@Configuration
@Profile({"!s3-mocks"})
public class AmazonS3Config {

	/**
	 * The config values used for s3.
	 */
	@Autowired
	private S3Config s3Config;

	/**
	 * Creating the AmazonS3 client
	 * @return The AmazonS3 client.
	 */
	@Bean
	public AmazonS3 createS3Client(){
		return AmazonS3ClientBuilder.standard().
				withCredentials(createAwsCredentialProvider())
				.withRegion(s3Config.getRegion())
				.build();
	}

	/**
	 * Creating an aws credential provider
	 * @return The AWSCredentialsProvider
	 */
	private AWSCredentialsProvider createAwsCredentialProvider(){
		return new StaticCredentialsProvider(
				new BasicAWSCredentials(s3Config.getAccessKey(),s3Config.getSecretKey()));
	}
	
}
