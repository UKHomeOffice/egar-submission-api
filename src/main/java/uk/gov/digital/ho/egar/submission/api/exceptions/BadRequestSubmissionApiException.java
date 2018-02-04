package uk.gov.digital.ho.egar.submission.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *  
 */
@ResponseStatus(value=HttpStatus.BAD_REQUEST) 
abstract public class BadRequestSubmissionApiException extends SubmissionApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public BadRequestSubmissionApiException() {
	}

	/**
	 * @param message
	 */
	public BadRequestSubmissionApiException(String message) {
		super(message);
	}

	public BadRequestSubmissionApiException(String message, Exception e) {
		super(message, e);
	}



}