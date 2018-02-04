package uk.gov.digital.ho.egar.submission.model.rest;

import org.springframework.stereotype.Component;

import lombok.Data;
import uk.gov.digital.ho.egar.submission.model.GarAttributes;

@Data
@Component("garAttributes")
public class GarAttributesPojo implements GarAttributes {

	private boolean hazardousGoodsOnBoard;
	private boolean flightWithinCTA;

}
