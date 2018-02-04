package uk.gov.digital.ho.egar.submission.model.rest;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.digital.ho.egar.submission.model.Gender;
import uk.gov.digital.ho.egar.submission.model.PeopleDetails;

@Data
public class PeopleDetailsPojo implements PeopleDetails{

	@JsonProperty("given_name")
	private String givenName;

	@JsonProperty("family_name")
	private String familyName;
	private Gender gender;
	private String address;
	private LocalDate dob;
	private String place;
	private String nationality;

	@JsonProperty("document_type")
	private String documentType;

	@JsonProperty("document_no")
	private String documentNo;

	@JsonProperty("document_expiryDate")
	private LocalDate documentExpiryDate;

	@JsonProperty("document_issuingCountry")
	private String documentIssuingCountry;
}
