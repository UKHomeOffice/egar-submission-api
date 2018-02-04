package uk.gov.digital.ho.egar.submission.api.exceptions;

/**
 * @author localuser
 *
 */
public class FileNotFoundSubmissionApiException extends DataNotFoundSubmissionApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public FileNotFoundSubmissionApiException(final String message)
	{
		super(message);
	}
	
}
