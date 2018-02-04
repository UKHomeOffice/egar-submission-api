package uk.gov.digital.ho.egar.submission.model;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public interface PeopleDetails {

	@NotNull
	@Size(max = 35)
	public String getGivenName();

	@NotNull
	@Size(max = 35)
	public String getFamilyName();

	public Gender getGender();

	@Size(max = 35)
	public String getAddress();

	@NotNull
	public LocalDate getDob();

	@NotNull
	@Size(max = 35)
	public String getPlace();

	@NotNull
	@Size(max = 3)
	public String getNationality();

	@NotNull
	//Should be enum of "Passport", "IdentityCard", "Other"
	public String getDocumentType();

	@NotNull
	@Size(max = 44)
	public String getDocumentNo();

	@NotNull
	public LocalDate getDocumentExpiryDate();

	@NotNull
	@Size(max = 3)
	public String getDocumentIssuingCountry();
} 