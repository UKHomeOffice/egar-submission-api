package uk.gov.digital.ho.egar.submission.client.jms;

import uk.gov.digital.ho.egar.submission.api.exceptions.EnqueueException;
import uk.gov.digital.ho.egar.submission.model.QueuedGarCancellationToCbp;
import uk.gov.digital.ho.egar.submission.model.QueuedGarSubmissionToCbp; 
 
public interface SubmissionExecutionService {
   
  public void processSubmission(final QueuedGarSubmissionToCbp queuedSubmission) throws EnqueueException;
  
  public void processCancellation (final QueuedGarCancellationToCbp queuedCancellation) throws EnqueueException;
 
} 