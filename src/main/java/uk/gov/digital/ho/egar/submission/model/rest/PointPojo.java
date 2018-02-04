package uk.gov.digital.ho.egar.submission.model.rest;

import lombok.Data;
import uk.gov.digital.ho.egar.submission.model.Point;

@Data
public class PointPojo implements Point {
	private String latitude;
	private  String longitude;

	/**
	 * Checks the validity of the point object supplied
	 * @param point The point object
	 * @return The validity of the point object
	 */
	public static boolean isPointValid(final Point point){
		if (point == null){
			return false;
		}

		//Both values are required to construct a LatLongPair object
		if (point.getLatitude() == null || point.getLongitude() == null){
			return false;
		}

		return true;
	}
}
