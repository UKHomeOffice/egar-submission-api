package uk.gov.digital.ho.egar.submission.model;

import cz.jirutka.validator.collection.constraints.EachNotNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 
 * @see PersonsSnapshotPojo.class
 */
public interface PersonsSubmissionRequest {

	@NotNull
	@Valid
	People getCaptain();

	@EachNotNull
	@Valid
	List<People> getCrew();

	@EachNotNull
	@Valid
	List<People> getPassengers();
}
