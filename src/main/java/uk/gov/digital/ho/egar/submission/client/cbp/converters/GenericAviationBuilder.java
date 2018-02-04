package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import com.fincore.cbp.stt.xml.T_GenericAviation;
import uk.gov.digital.ho.egar.submission.api.exceptions.MissingMandatoryFieldException;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionRequest;

public interface GenericAviationBuilder {
    T_GenericAviation build(GarSubmissionRequest garSnapshot) throws MissingMandatoryFieldException;
}
