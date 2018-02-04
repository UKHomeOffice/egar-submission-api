package uk.gov.digital.ho.egar.submission.model.rest;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.submission.model.GeographicLocation;
import uk.gov.digital.ho.egar.submission.model.Point;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
public class GeographicLocationPojo implements GeographicLocation {

	public GeographicLocationPojo() {
	}

	protected GeographicLocationPojo(GeographicLocation loc) {
		legNumber = loc.getLegNumber();
		legCount = loc.getLegCount();
		datetime = loc.getDatetime();
		iCAOCode = loc.getICAOCode();
		iATACode = loc.getIATACode();
		point = loc.getPoint();
	}

	@JsonProperty(value = "leg_num")
	private Integer legNumber;

    @JsonProperty(value = "leg_count")
	private Integer legCount;

	@ApiModelProperty(dataType = "String")
	private ZonedDateTime datetime;

    @JsonProperty(value = "ICAO")
	private String iCAOCode;

    @JsonProperty(value = "IATA")
	private String iATACode;

    @JsonDeserialize(as = PointPojo.class)
	private Point point;
}
