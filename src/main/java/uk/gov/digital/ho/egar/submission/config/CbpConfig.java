package uk.gov.digital.ho.egar.submission.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class CbpConfig {

	 private  static final String URL_KEY = "cbp.url";
	 private   static final String USERNAME_KEY = "cbp.username";
	 private   static final String PASSWORD_KEY = "cbp.password";
	 
	@Value("${cbp.cancellation.enabled}")
	private boolean enableCancellation;
		
	@Autowired
    private Environment env;

	public String getUserNameKey() {
		return env.getProperty(USERNAME_KEY);
	}

	public String getUserPasswordKey() {
		return env.getProperty(PASSWORD_KEY);
	}

	public String getUserUrlKey() {
		return env.getProperty(URL_KEY);
	}

	public boolean isEnableCancellation() {
		return enableCancellation;
	}
	
	
	
}
