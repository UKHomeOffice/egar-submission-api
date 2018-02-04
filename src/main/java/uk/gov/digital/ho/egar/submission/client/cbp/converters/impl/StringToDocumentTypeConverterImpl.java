package uk.gov.digital.ho.egar.submission.client.cbp.converters.impl;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fincore.cbp.stt.xml.T_DocumentType;

import uk.gov.digital.ho.egar.submission.client.cbp.converters.StringToDocumentTypeConverter;

@Component
public class StringToDocumentTypeConverterImpl implements Converter<String, T_DocumentType>, StringToDocumentTypeConverter {

    public static final String PASSPORT_DOCUMENT_TYPE = "passport";
    public static final String UNSPECIFIED_DOCUMENT_TYPE = "unspecified";

    @Override
    public T_DocumentType convert(String documentType) {
        if (documentType.toLowerCase().equals(PASSPORT_DOCUMENT_TYPE)) {
            return T_DocumentType.PASSPORT;
        }
        if (documentType.toLowerCase().equals(UNSPECIFIED_DOCUMENT_TYPE)) {
            return T_DocumentType.OTHER;
        }
        return T_DocumentType.IDENTITY_CARD;
    }
}

