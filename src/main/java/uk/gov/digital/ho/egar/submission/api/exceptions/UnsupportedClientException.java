package uk.gov.digital.ho.egar.submission.api.exceptions;

public class UnsupportedClientException extends DataNotFoundSubmissionApiException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedClientException (final String message) {
		super(message);
	}
}
