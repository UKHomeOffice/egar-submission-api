package uk.gov.digital.ho.egar.submission.model.rest;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import cz.jirutka.validator.collection.constraints.EachNotNull;
import lombok.Data;
import uk.gov.digital.ho.egar.submission.model.Aircraft;
import uk.gov.digital.ho.egar.submission.model.GeographicLocation;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionRequest;
import uk.gov.digital.ho.egar.submission.model.PersonsSubmissionRequest;
import uk.gov.digital.ho.egar.submission.model.SupportingFilesSubmissionRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class GarSubmissionRequestPojo implements GarSubmissionRequest {
	
	@JsonProperty("gar_uuid")
	@NotNull
	private UUID garUuid;

	@NotNull
	@Valid
	@JsonDeserialize(as=AircraftRestPojo.class)
	private Aircraft aircraft;

	@NotNull
	@EachNotNull
	@Size(min=2, message = "size must be 2 or more")
	@Valid
	@JsonProperty("location")
	@JsonDeserialize(contentAs= GeographicLocationPojo.class)
	private List<GeographicLocation> locations;

	@NotNull
    @Valid
	@JsonProperty("people")
	@JsonDeserialize(as= PersonsSubmissionRequestPojo.class)
	private PersonsSubmissionRequest persons;
	
	@JsonProperty("files")
	@JsonDeserialize(contentAs=SupportingFilesSubmissionRequestPojo.class)
	private List<SupportingFilesSubmissionRequest> supportingFiles;

	@NotNull
    @Valid
	@JsonProperty("attributes")
	private Attribute attributes;
}
