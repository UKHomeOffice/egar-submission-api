package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import com.fincore.cbp.stt.xml.T_CommonSegment;
import uk.gov.digital.ho.egar.submission.api.exceptions.MissingMandatoryFieldException;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionRequest;

public interface BuildCommonSegFromSubmissionRequest {
    T_CommonSegment build(GarSubmissionRequest garSnapshot) throws MissingMandatoryFieldException;
}
