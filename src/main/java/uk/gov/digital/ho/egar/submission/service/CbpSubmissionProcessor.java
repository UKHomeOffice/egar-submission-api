package uk.gov.digital.ho.egar.submission.service;

import uk.gov.digital.ho.egar.submission.model.QueuedGarCancellationToCbp;
import uk.gov.digital.ho.egar.submission.model.QueuedGarSubmissionToCbp;

import java.io.IOException;

public interface CbpSubmissionProcessor {
    void processSubmissionRequests(QueuedGarSubmissionToCbp submission) throws IOException;

    void processCancellationRequest(QueuedGarCancellationToCbp cancellation);
}
