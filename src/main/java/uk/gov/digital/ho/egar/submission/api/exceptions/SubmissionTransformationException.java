package uk.gov.digital.ho.egar.submission.api.exceptions;

import java.util.UUID;

/**
 * The Submission transformation exception is thrown when the API is unable to create a valid
 * xml request to send to CBP.
 *
 * It extends from the abstract BadRequestSubmissionApi which will map to a 400 response code.
 * @author localuser
 *
 */
public class SubmissionTransformationException extends BadRequestSubmissionApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public SubmissionTransformationException(final UUID garUuid , final UUID userId, final Exception e )
	{
		super(String.format("Cannot transform request for gar with id '%s' for user '%s'", garUuid == null?"Unknown":garUuid, userId), e);
	}

	public SubmissionTransformationException(final String message, final Exception e )
	{
		super(message, e);
	}

	public SubmissionTransformationException(final String message)
	{
		super(message);
	}

}
