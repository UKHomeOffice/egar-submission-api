package uk.gov.digital.ho.egar.submission.api.exceptions;

/**
 * @author localuser
 *
 */
public class MissingMandatoryFieldException extends BadRequestSubmissionApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public MissingMandatoryFieldException(final String message)
	{
		super(message);
	}
	
}
