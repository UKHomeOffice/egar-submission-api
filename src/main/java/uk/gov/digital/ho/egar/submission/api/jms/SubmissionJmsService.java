package uk.gov.digital.ho.egar.submission.api.jms;


import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.digital.ho.egar.submission.constants.QueueNames;
import uk.gov.digital.ho.egar.submission.model.jms.QueuedGarCancellationToCbpPojo;
import uk.gov.digital.ho.egar.submission.model.jms.QueuedGarSubmissionToCbpPojo;
import uk.gov.digital.ho.egar.submission.service.CbpSubmissionProcessor;
import uk.gov.digital.ho.egar.submission.service.impl.CbpSubmissionProcessorImpl;

@Service
@Profile("!disable-jms")
public class SubmissionJmsService implements SubmissionService {

    private static Log logger = LogFactory.getLog(CbpSubmissionProcessorImpl.class);

    @Autowired
    private CbpSubmissionProcessor submissionProcessor;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Jms listner method that is used by Spring to process any items in the JMS queue
     * @param submission
     */

    @Override
    @JmsListener(destination = QueueNames.CBP_SUBMISSION_REQUEST_QUEUE_REF)
    public void processSubmission(String request) throws JMSException {
        QueuedGarSubmissionToCbpPojo submission = null;
        try {
            submission = objectMapper.readValue(request, QueuedGarSubmissionToCbpPojo.class);

            logger.debug(String.format("Processing JMS queue request with submission id '%s'", submission.getSubmissionUuid()));

            submissionProcessor.processSubmissionRequests(submission);
        }catch (Exception e){
            String errorMessage = "Encountered error when attempting to perform submission.";
            if (submission!=null){
                errorMessage = String.format("Encountered error when attempting to perform submission '%s'.", submission.getSubmissionUuid());
            }
            logger.error(errorMessage, e);
            throw new JMSException(errorMessage);
        }
    }

    @Override
    @JmsListener(destination = QueueNames.CBP_CANCEL_REQUEST_QUEUE_REF)
    public void processCancellation(String request) throws JMSException {
        QueuedGarCancellationToCbpPojo cancellation = null;
        try {
            cancellation = objectMapper.readValue(request, QueuedGarCancellationToCbpPojo.class);
            submissionProcessor.processCancellationRequest(cancellation);
        } catch (Exception e) {
            String errorMessage = "Encountered error when attempting to perform submission.";
            if (cancellation!=null){
                errorMessage = String.format("Encountered error when attempting to perform submission'%s'.", cancellation.getSubmissionUuid());
            }
            logger.error(errorMessage, e);
            throw new JMSException(errorMessage);
        }
    }
}
