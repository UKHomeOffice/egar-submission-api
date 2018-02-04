package uk.gov.digital.ho.egar.submission.utils;

public interface SexagecimalUtils {

    String coordToDMS(double coord, Dimension dim);

    String coordToDM(double coord, Dimension dim);

    public static enum Dimension{
        LAT,
        LONG
    }
}
