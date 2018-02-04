package uk.gov.digital.ho.egar.submission.client.jms.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.submission.client.jms.SubmissionExecutionService;
import uk.gov.digital.ho.egar.submission.model.QueuedGarCancellationToCbp;
import uk.gov.digital.ho.egar.submission.model.QueuedGarSubmissionToCbp;
import uk.gov.digital.ho.egar.submission.service.impl.CbpSubmissionProcessorImpl;

@Component 
@Profile({"disable-jms"}) 
public class MockQueueSubmission implements SubmissionExecutionService {

	@Autowired
	private CbpSubmissionProcessorImpl process;
	
	@Override
	public void processSubmission(QueuedGarSubmissionToCbp queuedSubmission) {
		
		try {
			process.processSubmissionRequests(queuedSubmission);
		} catch (IOException e) {
			e.getMessage();
		}
	}

	@Override
	public void processCancellation(QueuedGarCancellationToCbp queuedCancellation) {

		process.processCancellationRequest(queuedCancellation);
	} 
	
}
