package uk.gov.digital.ho.egar.submission.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * @author localuser
 *
 */
@ResponseStatus(value= HttpStatus.FORBIDDEN)
public class BadOperationSubmissionApiException extends DataNotFoundSubmissionApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public BadOperationSubmissionApiException(String message) {
		super(message);
	}

	
}
