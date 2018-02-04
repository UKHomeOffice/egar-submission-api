package uk.gov.digital.ho.egar.submission.constants;

public interface LatitudeLongitudeRegex {
    public static final String LONGITUDE_DECIMAL_REGEX = "^([+-]?(0?[0-9]?[0-9].[0-9]{2}|1[0-7][0-9].[0-9][0-9]|180.00))$";
    public static final String LONGITUDE_DEGREES_REGEX = "^([EW](0?[0-9]{2}[0-5][0-9]|1[0-7][0-9][0-5][0-9]|18000))$";

    public static final String LATITUDE_DECIMAL_REGEX = "^([+-]?([0-8]?[0-9].[0-9]{2}|90.00))$";
    public static final String LATITUDE_DEGREES_REGEX = "^([NS]([0-8][0-9][0-5][0-9]|9000))$";
}
