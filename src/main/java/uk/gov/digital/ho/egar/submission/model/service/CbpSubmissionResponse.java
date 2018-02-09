package uk.gov.digital.ho.egar.submission.model.service;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * A cut down version of one of the SOAP objects.
 */
@Data
@Builder
@ToString
public class CbpSubmissionResponse {
    private boolean success;
    /** Reason is a free text value from CBP. */
    private String reason;
    private String identifier;
}
