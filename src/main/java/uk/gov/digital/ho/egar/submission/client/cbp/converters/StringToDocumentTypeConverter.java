package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import com.fincore.cbp.stt.xml.T_DocumentType;

public interface StringToDocumentTypeConverter {
    T_DocumentType convert(String documentType);
}
