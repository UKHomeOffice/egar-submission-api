package uk.gov.digital.ho.egar.submission.api.exceptions;

import uk.gov.digital.ho.egar.shared.util.exceptions.NoCallStackException;

/**
 * A base exception type that does not pick uo the stack trace.
 *
 */
public class SubmissionApiException extends NoCallStackException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public SubmissionApiException() {
		this(null,null);
	}

	public SubmissionApiException(String message) {
		this(message,null);
	}

	public SubmissionApiException(Throwable cause) {
		this(null,cause);
	}

	public SubmissionApiException(String message, Throwable cause) {
		super(message, cause);
        }

}
