package uk.gov.digital.ho.egar.submission.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration values required for S3.
 */
@Getter
@Configuration
@Profile("!mocks")
public class SqsConfig {

	@Value("${aws.sqs.access.key}")
	private String accessKey;

	@Value("${aws.sqs.secret.key}")
	private String secretKey;

	@Value("${aws.sqs.region}")
	private String region;

}
