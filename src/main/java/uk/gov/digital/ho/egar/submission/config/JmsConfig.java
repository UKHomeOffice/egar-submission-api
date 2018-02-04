package uk.gov.digital.ho.egar.submission.config;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.Session;


@Configuration
@EnableJms
@Profile("!jms-mocks")
public class JmsConfig {

	@Autowired
	private SQSConnectionFactory sqsConnectionFactory;

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory =
				new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(this.sqsConnectionFactory);
		factory.setDestinationResolver(new DynamicDestinationResolver());
		factory.setConcurrency("3-10");
		factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
		return factory;
	}

	@Bean
	public JmsTemplate defaultJmsTemplate() {
		return new JmsTemplate(this.sqsConnectionFactory);
	}

}
