package uk.gov.digital.ho.egar.submission.client.cbp.mock;

import java.util.UUID;

import com.fincore.cbpcarrierwebservice.entities.ArrayOfFile;

import lombok.Getter;
import lombok.Setter;
import uk.gov.digital.ho.egar.submission.client.cbp.CbpClient;
import uk.gov.digital.ho.egar.submission.model.service.CbpSubmissionResponse;

/**
 * For use when running the Mock profile & data is not being sent to a CBP end point.
 */
public class MockCbpClient implements CbpClient{

	@Getter
	private String lastRequest = null;

	@Getter
	@Setter
	private boolean success = true;

	@Getter
	@Setter
	private boolean exception = false;

	private static final String FAILED_REASON = "Submission failed";
	private static final String SUCCESS_REASON = "Success";

	@Override
	public CbpSubmissionResponse submitSTTXML(String cbpXml) {

		if (exception){
			throw new RuntimeException("Exception");
		}

		lastRequest = cbpXml;

        CbpSubmissionResponse response = CbpSubmissionResponse.builder()
				.identifier(UUID.randomUUID().toString())
				.success(this.success)
				.reason(this.success?SUCCESS_REASON:FAILED_REASON)
				.build();

		return response;
	}

	@Override
	public CbpSubmissionResponse cancelSubmittedGar(String externalSubmissionRef) {

		if (exception){
			throw new RuntimeException("Exception");
		}

		CbpSubmissionResponse response = CbpSubmissionResponse.builder()
				.identifier(UUID.randomUUID().toString())
				.success(this.success)
				.reason(this.success?SUCCESS_REASON:FAILED_REASON)
				.build();

		return response;
	}
	
	public boolean isCBPServiceAvailable() {
		return true;
	}

	@Override
	public CbpSubmissionResponse submitWithAttachments(ArrayOfFile files) {
		CbpSubmissionResponse response = CbpSubmissionResponse.builder()
				.identifier(UUID.randomUUID().toString())
				.success(this.success)
				.reason(this.success?SUCCESS_REASON:FAILED_REASON)
				.build();

		return response;
	}

}
