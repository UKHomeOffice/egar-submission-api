package uk.gov.digital.ho.egar.submission.api.exceptions;

import java.util.UUID;

/**
 * @author localuser
 *
 */
public class SubmissionNotFoundSubmissionApiException extends DataNotFoundSubmissionApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public SubmissionNotFoundSubmissionApiException(final UUID submissionUuid , final UUID userId )
	{
		super(String.format("Can not find submission %s for user %s", submissionUuid.toString(), userId.toString()));
	}
	
}
