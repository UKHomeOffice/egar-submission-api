package uk.gov.digital.ho.egar.submission.model;

import uk.gov.digital.ho.egar.submission.model.validation.SingleLocationValidator;

import javax.validation.constraints.Pattern;

public interface Point {
	@Pattern(regexp = SingleLocationValidator.ValidLatitude.REGEX, message = SingleLocationValidator.ValidLatitude.MSG)
	public String getLatitude();

	@Pattern(regexp = SingleLocationValidator.ValidLongitude.REGEX, message = SingleLocationValidator.ValidLongitude.MSG)
	public String getLongitude();
}
