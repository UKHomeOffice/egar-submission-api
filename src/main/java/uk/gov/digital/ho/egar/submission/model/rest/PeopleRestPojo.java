package uk.gov.digital.ho.egar.submission.model.rest;

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.submission.model.People;
import uk.gov.digital.ho.egar.submission.model.PeopleDetails;
import uk.gov.digital.ho.egar.submission.model.TravellerType;

@Data
@EqualsAndHashCode(callSuper=false)
public class PeopleRestPojo implements People {
	private TravellerType type;
	private UUID uuid;
	@JsonDeserialize(as=PeopleDetailsPojo.class)
	private PeopleDetails details;
}