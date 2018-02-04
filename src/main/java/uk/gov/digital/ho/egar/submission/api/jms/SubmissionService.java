package uk.gov.digital.ho.egar.submission.api.jms;

import javax.jms.JMSException;

public interface SubmissionService {

    void processSubmission(String submission) throws JMSException;

    void processCancellation(String cancellation) throws JMSException;
}
