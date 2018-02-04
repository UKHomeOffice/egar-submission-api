package uk.gov.digital.ho.egar.submission.utils.impl;

import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.submission.utils.SexagecimalUtils;

@Component
public class SexagecimalUtilsImpl implements SexagecimalUtils {

    @Override
    public String coordToDMS(double coord, Dimension dim) {
        assert (dim != null);

        String[] dirs = getDirections(dim);
        String dir =dirs[coord >= 0 ? 0 : 1];

        double abs = Math.abs(coord);
        double whole = Math.floor(abs);
        double fraction = abs - whole;
        double fractionMinutes = fraction * 60;
        double minutes = Math.floor(fractionMinutes);
        double seconds = Math.floor((fractionMinutes - minutes) * 60);

        return dir + whole + minutes + seconds;
    }

    @Override
    public String coordToDM(double coord, Dimension dim) {
        assert (dim != null);

        String[] dirs = getDirections(dim);
        String dir =dirs[coord >= 0 ? 0 : 1];

        double abs = Math.abs(coord);
        double whole = Math.floor(abs);
        double fraction = abs - whole;
        double fractionMinutes = fraction * 60;
        double minutes = Math.round(fractionMinutes);

        String strDeg = String.format("%02d", (int) whole);
        String strMin = String.format("%02d", (int) minutes);

        return dir + strDeg + strMin;
    }

    private String[] getDirections(Dimension dim) {
        if (dim == Dimension.LAT) {
            return new String[]{"N", "S"};
        } else {
            return new String[]{"E", "W"};
        }
    }
}
