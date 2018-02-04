package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import com.fincore.cbp.stt.xml.T_LatLongPair;
import org.junit.Before;
import org.junit.Test;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.impl.PointToLatLongPairConverterImpl;
import uk.gov.digital.ho.egar.submission.model.rest.PointPojo;
import uk.gov.digital.ho.egar.submission.utils.impl.SexagecimalUtilsImpl;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class PointToLatLongPairConverterTest {

    private PointToLatLongPairConverter underTest;

    @Before
    public void setup(){
        underTest = new PointToLatLongPairConverterImpl(new SexagecimalUtilsImpl());
    }

    @Test
    public void testDecimalToDegreePointConversion(){
        PointPojo point = new PointPojo();
        point.setLatitude("2.22");
        point.setLongitude("28.33");

        Optional<T_LatLongPair> result = underTest.convert(point);

        assertTrue(result.isPresent());
        assertEquals(result.get().getLat(), "N0213");
        assertEquals(result.get().getLong(), "E2820");
    }

    @Test
    public void testNegativeDecimalToDegreePointConversion(){
        PointPojo point = new PointPojo();
        point.setLatitude("-2.22");
        point.setLongitude("-28.33");

        Optional<T_LatLongPair> result = underTest.convert(point);

        assertTrue(result.isPresent());
        assertEquals(result.get().getLat(), "S0213");
        assertEquals(result.get().getLong(), "W2820");
    }


    @Test
    public void testMixedPointConversion(){
        PointPojo point = new PointPojo();
        point.setLatitude("-2.22");
        point.setLongitude("W1234");

        Optional<T_LatLongPair> result = underTest.convert(point);

        assertTrue(result.isPresent());
        assertEquals(result.get().getLat(), "S0213");
        assertEquals(result.get().getLong(), "W1234");
    }

    @Test
    public void testMixedPointConversion2(){
        PointPojo point = new PointPojo();
        point.setLatitude("N1234");
        point.setLongitude("28.33");

        Optional<T_LatLongPair> result = underTest.convert(point);

        assertTrue(result.isPresent());
        assertEquals(result.get().getLat(), "N1234");
        assertEquals(result.get().getLong(), "E2820");
    }
}
