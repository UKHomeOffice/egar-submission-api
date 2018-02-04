package uk.gov.digital.ho.egar.submission.model.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.h2.util.StringUtils;

import uk.gov.digital.ho.egar.submission.model.GeographicLocation;
import uk.gov.digital.ho.egar.submission.model.rest.PointPojo;

/**
 * Does the validation for a Location.
 */
public class SingleLocationValidator implements ConstraintValidator<SingleLocationPresent, GeographicLocation> {

    /**
     * The ICAO (/ˌaɪˌkeɪˈoʊ/, eye-KAY-oh) airport code or location indicator is a four-letter code designating aerodromes around the world.
     * A utility class just to hold the validation.
     * Validation for
     * <pre>
     * {
     * "datetime":"YYYY-MM-DDThh:mm:ssZ",
     * "ICAO":"CODE"
     * }
     * </pre>
     *
     * @see https://en.wikipedia.org/wiki/ICAO_airport_code
     */
    public static class ValidIcao {
        public static final String REGEX = "^[0-9a-zA-Z\\-]{1,13}$";
        public static final String MSG = "Airport ICAO is not the correct format.";
    }
    
    /**
     * The IATA airport code or location identifier is a three-letter code designating many airports around the world.
     * A utility class just to hold the validation.
     * Validation for
     * <pre>
     * {
     * "datetime":"YYYY-MM-DDThh:mm:ssZ",
     * "IATA":"CODE"
     * }
     * </pre>
     *
     * @see https://en.wikipedia.org/wiki/IATA_airport_code
     */
    public static class ValidIata {
        public static final String REGEX = "^[0-9a-zA-Z\\-]{1,13}$";
        public static final String MSG = "Airport IATA is not the correct format.";
    }
    

    public static class ValidLatitude {
        public static final String REGEX = "^(([NS]([0-8][0-9][0-5][0-9]|9000))|([+-]?([0-8]?[0-9].[0-9]{2}|90.00)))$";
        public static final String MSG = "Latitude does not match the decimal or degrees minutes formats.";
    }

    public static class ValidLongitude {
        public static final String REGEX = "^(([EW](0?[0-9]{2}[0-5][0-9]|1[0-7][0-9][0-5][0-9]|18000))|([+-]?(0?[0-9]?[0-9].[0-9]{2}|1[0-7][0-9].[0-9][0-9]|180.00)))$";
        public static final String MSG = "Longitude does not match the decimal or degrees minutes formats.";
    }


    /**
     * A utility class just to hold the validation.
     */
    private static class LocationPresent {
        static int countLocationDetails(GeographicLocation loc) {
            int locationPresentCount = 0;
            if (loc.getPoint() != null && PointPojo.isPointValid(loc.getPoint())) {
                locationPresentCount++;
            }

            if (!StringUtils.isNullOrEmpty(loc.getICAOCode())) {
                locationPresentCount++;
            }

            if (!StringUtils.isNullOrEmpty(loc.getIATACode())) {
                locationPresentCount++;
            }

            return locationPresentCount;
        }
    }


    @Override
    public void initialize(SingleLocationPresent singleLocationPresent) {

    }

    /**
     * Actually perform the validation.
     */
    @Override
    public boolean isValid(GeographicLocation loc, ConstraintValidatorContext ctx) {

        if (loc == null)
            // Nothing so this is ok
            return true;

        int locations = LocationPresent.countLocationDetails(loc);

        boolean isOk = locations == 1;

        boolean isDefaultMessage = "".equals(ctx
                .getDefaultConstraintMessageTemplate());

        if (!isOk && isDefaultMessage) {
            ctx.disableDefaultConstraintViolation();

            ctx
                    .buildConstraintViolationWithTemplate(locations + " locations are present, requires 1")
                    .addBeanNode()
                    .addConstraintViolation();
        }

        return isOk;
    }

}
