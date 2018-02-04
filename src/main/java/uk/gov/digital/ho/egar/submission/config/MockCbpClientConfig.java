package uk.gov.digital.ho.egar.submission.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import uk.gov.digital.ho.egar.submission.client.cbp.CbpClient;
import uk.gov.digital.ho.egar.submission.client.cbp.mock.MockCbpClient;

/**
 * Makes the mock CBP submission client available for test/development
 * @author Keshava.Grama
 *
 */
@Configuration
@Profile({"cbp-mocks"})
public class MockCbpClientConfig implements CbpClientConfig {
	Log logger = LogFactory.getLog(MockCbpClientConfig.class);
	
	/* (non-Javadoc)
	 * @see uk.gov.digital.ho.egar.submission.config.CbpClientConfig#cbpClientMock()
	 */
	@Override
	@Bean
	@Primary
	public CbpClient cbpClient() {
		logger.info("initialised mock CBP client");
		MockCbpClient retVal = new MockCbpClient();
		return retVal;
	}
}
