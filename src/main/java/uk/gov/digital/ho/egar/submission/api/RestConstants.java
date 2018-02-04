package uk.gov.digital.ho.egar.submission.api;

import uk.gov.digital.ho.egar.constants.ServicePathConstants;

public interface RestConstants {

	String SUBMISSION_TYPE = "CBP_STT";
	String ROOT_SERVICE_NAME = "Submission";
	String ROOT_PATH = ServicePathConstants.ROOT_PATH_SEPERATOR + ServicePathConstants.ROOT_SERVICE_API
			+ ServicePathConstants.ROOT_PATH_SEPERATOR + ServicePathConstants.SERVICE_VERSION_ONE
			+ ServicePathConstants.ROOT_PATH_SEPERATOR + ROOT_SERVICE_NAME;
	String PATH_SUBMIT = "/";
	String PATH_PREVIOUS_SUBMISSION = "submission_uuid";
	String PATH_PREVIOUS_SUBMISSION_PATH = ServicePathConstants.ROOT_PATH_SEPERATOR +"{" + PATH_PREVIOUS_SUBMISSION + "}" + ServicePathConstants.ROOT_PATH_SEPERATOR;

}