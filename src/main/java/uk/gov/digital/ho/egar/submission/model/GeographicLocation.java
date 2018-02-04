package uk.gov.digital.ho.egar.submission.model;

import java.time.ZonedDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import uk.gov.digital.ho.egar.submission.model.validation.SingleLocationPresent;
import uk.gov.digital.ho.egar.submission.model.validation.SingleLocationValidator;

@ApiModel
@SingleLocationPresent
public interface GeographicLocation {

	public Integer getLegNumber();
	public Integer getLegCount();
	@NotNull
	public ZonedDateTime getDatetime();

	@Pattern(regexp = SingleLocationValidator.ValidIcao.REGEX, message = SingleLocationValidator.ValidIcao.MSG)
	@Size(max = 13)
	public String getICAOCode();

	@Pattern(regexp = SingleLocationValidator.ValidIata.REGEX, message = SingleLocationValidator.ValidIata.MSG)
	@Size(max = 13)
	public String getIATACode();
	
	@Valid
	public Point getPoint();
}
