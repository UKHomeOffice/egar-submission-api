package uk.gov.digital.ho.egar.submission.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface People {
	@NotNull
	public TravellerType getType();

	@NotNull
	@Valid
	public PeopleDetails getDetails();

} 

