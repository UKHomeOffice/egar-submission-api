package uk.gov.digital.ho.egar.submission.client.cbp.converters.impl;

import java.io.StringWriter;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fincore.cbp.stt.xml.ObjectFactory;
import com.fincore.cbp.stt.xml.T_CommonSegment;
import com.fincore.cbp.stt.xml.T_EBorders_STTDocument;

import uk.gov.digital.ho.egar.submission.api.exceptions.MissingMandatoryFieldException;
import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionApiException;
import uk.gov.digital.ho.egar.submission.client.cbp.CbpConstants;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.BuildCommonSegFromSubmissionRequest;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.GarXmlMarshaller;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.GenericAviationBuilder;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.ZonedDateTimeToXMLGregorianConverter;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionRequest;
import uk.gov.digital.ho.egar.submission.model.jms.SubmittingUser;

/**
 * STT xml generator for use with CBP submissions without any attachments.
 */
@Component
public class GarXmlMarshallerImpl implements GarXmlMarshaller {
    private ObjectFactory objFac = new ObjectFactory();

    private final GenericAviationBuilder airplaneConv;

    private final BuildCommonSegFromSubmissionRequest commonSegBuilder;

    private final ZonedDateTimeToXMLGregorianConverter dateConv;

    public GarXmlMarshallerImpl(@Autowired final GenericAviationBuilder airplaneConv,
                           @Autowired final BuildCommonSegFromSubmissionRequest commonSegBuilder,
                           @Autowired final ZonedDateTimeToXMLGregorianConverter dateConv) {
		this.airplaneConv = airplaneConv;
		this.commonSegBuilder = commonSegBuilder;
		this.dateConv = dateConv;
    }


    @Override
	public String marshalGarSubmissionRequest(
            GarSubmissionRequest garSnapshot, SubmittingUser userValues, String manifestType, ZonedDateTime manifestTime) throws SubmissionApiException
    {
    	
        T_EBorders_STTDocument sttDoc = convertFromGarSnapshot(garSnapshot, manifestType, manifestTime);
        
        try {
			StringWriter xmlRep = new StringWriter();
			
			JAXBContext jaxbContext = JAXBContext.newInstance(T_EBorders_STTDocument.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			jaxbMarshaller.marshal(objFac.createEBorders_STTDocument(sttDoc), xmlRep);
			
			return xmlRep.toString();
		} catch (JAXBException ex) {
			throw new SubmissionApiException(ex);
		}
    }

    private T_EBorders_STTDocument convertFromGarSnapshot(GarSubmissionRequest garSnapshot, 
    												String manifestType,
    												ZonedDateTime manifestTime) throws SubmissionApiException {
        T_EBorders_STTDocument sttDoc = new T_EBorders_STTDocument();

        //Sets the common section
        T_CommonSegment commonSeg = commonSegBuilder.build(garSnapshot);
        sttDoc.setCommonSegment(commonSeg);

        //Sets the manifest information
        populateManifestInformation(manifestType, manifestTime, commonSeg);

        //Sets the aviation information
        sttDoc.setGenericAviation(airplaneConv.build(garSnapshot));

        sttDoc.setSchemaVersion(CbpConstants.STT_XML_VERSION);

        return sttDoc;
    }

    private void populateManifestInformation(String manifestType, ZonedDateTime manifestTime, T_CommonSegment commonSeg) throws MissingMandatoryFieldException {
        commonSeg.setManifestType(manifestType);
        commonSeg.setManifestNo(BigInteger.ZERO);

        Optional<XMLGregorianCalendar> dateTimeConv = dateConv.convert(manifestTime);
        if (dateTimeConv.isPresent()) {
            commonSeg.setManifestDate(dateTimeConv.get());
            commonSeg.setManifestTime(dateTimeConv.get());
        } else {
            throw new MissingMandatoryFieldException("Missing manifest date time information.");
        }
    }
}
