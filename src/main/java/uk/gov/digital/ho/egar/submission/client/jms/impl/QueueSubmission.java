package uk.gov.digital.ho.egar.submission.client.jms.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.digital.ho.egar.submission.api.exceptions.EnqueueException;
import uk.gov.digital.ho.egar.submission.client.jms.SubmissionExecutionService;
import uk.gov.digital.ho.egar.submission.constants.QueueNames;
import uk.gov.digital.ho.egar.submission.model.QueuedGarCancellationToCbp;
import uk.gov.digital.ho.egar.submission.model.QueuedGarSubmissionToCbp; 

@Component 
@Profile({"!disable-jms"}) 
public class QueueSubmission implements SubmissionExecutionService {
	private static Log logger = LogFactory.getLog(QueueSubmission.class);

	private final JmsTemplate jmsSender;

	private final ObjectMapper objectMapper;
	
	private final QueueNames queueNames;

	@Autowired
	public QueueSubmission(@Autowired JmsTemplate jmsSender,
						   @Autowired ObjectMapper objectMapper,
						   @Autowired QueueNames queueNames) {
		this.jmsSender = jmsSender;
		this.objectMapper = objectMapper;
		this.queueNames = queueNames;
	}
	@Override 
	public void processSubmission(QueuedGarSubmissionToCbp queuedSubmission) throws EnqueueException {
		try {
			String queueRequest = objectMapper.writeValueAsString(queuedSubmission);
			jmsSender.convertAndSend(queueNames.getCbpSubmissionRequestQueueName(), queueRequest);
		} catch (JmsException | JsonProcessingException e) {
			throw new EnqueueException("Unable to queue the submission message", e);
		}
	}

	@Override
	public void processCancellation(QueuedGarCancellationToCbp queuedCancellation) throws EnqueueException {
		try {
			String queueRequest = objectMapper.writeValueAsString(queuedCancellation);
			jmsSender.convertAndSend(queueNames.getCbpCancelRequestQueueName(), queueRequest);
		} catch (JmsException | JsonProcessingException e) {
			throw new EnqueueException("Unable to queue the cancellation message", e);
		}
	}

} 