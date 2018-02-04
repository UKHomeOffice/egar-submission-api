package uk.gov.digital.ho.egar.submission.client.cbp.converters.impl;

import java.util.Optional;

import com.fincore.cbp.stt.xml.T_RestrictedGoods;


import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.submission.client.cbp.converters.AttributesToTRestrictedGoodsConverter;
import uk.gov.digital.ho.egar.submission.model.rest.Attribute;

@Component
public class AttributesToTRestrictedGoodsConverterImpl implements Converter<Attribute, Optional<T_RestrictedGoods>>, AttributesToTRestrictedGoodsConverter {

	@Override
	public Optional<T_RestrictedGoods> convert(Attribute attributes) {
		T_RestrictedGoods returnVal = null;
		if (attributes != null && attributes.getHazardous() != null) {
			returnVal = new T_RestrictedGoods();
			returnVal.setGoodsExist(attributes.getHazardous());
		}
		return Optional.ofNullable(returnVal);
	}
}

