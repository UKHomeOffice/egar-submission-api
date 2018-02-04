package uk.gov.digital.ho.egar.submission.model.rest;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;
import uk.gov.digital.ho.egar.submission.model.People;
import uk.gov.digital.ho.egar.submission.model.PersonsSubmissionRequest;

@Data
public class PersonsSubmissionRequestPojo implements PersonsSubmissionRequest {
	@JsonDeserialize(as=PeopleRestPojo.class)
	private People captain;
	@JsonDeserialize(contentAs=PeopleRestPojo.class)
	private List<People> crew;
	@JsonDeserialize(contentAs=PeopleRestPojo.class)
    private List<People> passengers;
}
