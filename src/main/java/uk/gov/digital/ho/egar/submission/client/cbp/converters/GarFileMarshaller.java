package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.fincore.cbpcarrierwebservice.entities.ArrayOfFile;

import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionApiException;
import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionTransformationException;
import uk.gov.digital.ho.egar.submission.model.QueuedGarSubmissionToCbp;

public interface GarFileMarshaller {
	/**
	 * Convert the submission JSON excluding the supporting files to Excel
	 * @param submission
	 * @return
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws SubmissionTransformationException
	 */
	ArrayOfFile convertPojoToExcel(QueuedGarSubmissionToCbp submission) throws IOException, SubmissionApiException, InvalidFormatException;

}