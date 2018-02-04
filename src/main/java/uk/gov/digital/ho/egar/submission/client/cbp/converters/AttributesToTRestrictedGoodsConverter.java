package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import java.util.Optional;

import com.fincore.cbp.stt.xml.T_RestrictedGoods;
import uk.gov.digital.ho.egar.submission.model.rest.Attribute;

public interface AttributesToTRestrictedGoodsConverter {
    Optional<T_RestrictedGoods> convert(Attribute attributes);
}
