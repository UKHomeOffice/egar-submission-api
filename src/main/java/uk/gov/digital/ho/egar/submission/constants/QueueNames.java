package uk.gov.digital.ho.egar.submission.constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class QueueNames {
    public static final String CBP_SUBMISSION_REQUEST_QUEUE_REF = "${submission.queue.name}";

    public static final String CBP_CANCEL_REQUEST_QUEUE_REF = "${cancellation.queue.name}";

    @Value(CBP_SUBMISSION_REQUEST_QUEUE_REF)
    private String cbpSubmissionRequestQueueName;

    @Value(CBP_CANCEL_REQUEST_QUEUE_REF)
    private String cbpCancelRequestQueueName;

}
