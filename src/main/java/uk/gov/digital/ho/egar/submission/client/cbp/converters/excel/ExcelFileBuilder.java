package uk.gov.digital.ho.egar.submission.client.cbp.converters.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.fincore.cbpcarrierwebservice.entities.File;

import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionApiException;
import uk.gov.digital.ho.egar.submission.model.QueuedGarSubmissionToCbp;

public interface ExcelFileBuilder {
	public static final String NAME_OF_SHEET_FOR_GAR = "GAR";
	public static final int ARRIVAL_ROW_INDEX  = 2;
	public static final int DEPARTURE_ROW_INDEX = 3;
	public static final int AIRCRAFT_DETAILS_ROW_INDEX = 4;
	public static final int CREW_DETAILS_ROW_INDEX = 8;
	public static final int DEFAULT_CREW_SPACE = 8;
	File convertPojoToExcel(QueuedGarSubmissionToCbp submission) throws SubmissionApiException, InvalidFormatException;

}