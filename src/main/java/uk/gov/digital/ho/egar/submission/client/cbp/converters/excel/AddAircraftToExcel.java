package uk.gov.digital.ho.egar.submission.client.cbp.converters.excel;

import org.apache.poi.xssf.usermodel.XSSFRow;

import uk.gov.digital.ho.egar.submission.api.exceptions.MissingMandatoryFieldException;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionRequest;

public interface AddAircraftToExcel {
	// REGISTRATION	<value>	TYPE <value>		CAPTAIN SURNAME <value>		USUAL BASE <value>

	public static int CELL_NUM_REG = 1;
	public static int CELL_NUM_TYPE = 3;
	public static int CELL_NUM_BASE = 7;
	
	public static int CELL_NUM_ROW_2_REASON_VISIT = 1;
	
	public void addAircraftToExcel(XSSFRow row, GarSubmissionRequest submission) throws MissingMandatoryFieldException;
	public void addAircraftToExcelRowTwo(XSSFRow row, GarSubmissionRequest submission) throws MissingMandatoryFieldException;
}
