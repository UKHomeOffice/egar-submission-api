package uk.gov.digital.ho.egar.submission.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *  
 */
@ResponseStatus(value=HttpStatus.BAD_REQUEST) 
abstract public class DataNotFoundSubmissionApiException extends SubmissionApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public DataNotFoundSubmissionApiException() {
	}

	/**
	 * @param message
	 */
	public DataNotFoundSubmissionApiException(String message) {
		super(message);
	}


}