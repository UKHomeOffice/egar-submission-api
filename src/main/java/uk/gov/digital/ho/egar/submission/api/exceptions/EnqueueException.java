package uk.gov.digital.ho.egar.submission.api.exceptions;

public class EnqueueException extends SubmissionApiException{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public EnqueueException(final String message, Exception e) {
		super(message, e);
	}
}
