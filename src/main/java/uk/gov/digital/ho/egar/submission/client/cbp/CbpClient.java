package uk.gov.digital.ho.egar.submission.client.cbp;

import com.fincore.cbpcarrierwebservice.entities.ArrayOfFile;

import uk.gov.digital.ho.egar.submission.model.service.CbpSubmissionResponse;

public interface CbpClient  {
	public CbpSubmissionResponse submitSTTXML(final String cbpXml) ;
	
	public CbpSubmissionResponse cancelSubmittedGar(String externalSubmissionRef);
	
	public boolean isCBPServiceAvailable();
	
	public CbpSubmissionResponse submitWithAttachments(final ArrayOfFile submissionFiles);
}
