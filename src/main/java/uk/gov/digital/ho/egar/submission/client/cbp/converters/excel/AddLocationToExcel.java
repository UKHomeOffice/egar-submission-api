package uk.gov.digital.ho.egar.submission.client.cbp.converters.excel;

import org.apache.poi.xssf.usermodel.XSSFRow;

import uk.gov.digital.ho.egar.submission.api.exceptions.MissingMandatoryFieldException;
import uk.gov.digital.ho.egar.submission.model.GeographicLocation;

public interface AddLocationToExcel {
	public void addLocationToExcelRow(XSSFRow row, GeographicLocation location) throws MissingMandatoryFieldException;
}
