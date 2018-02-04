package uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.impl;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fincore.cbp.stt.xml.T_GenericAviation;

import uk.gov.digital.ho.egar.submission.api.exceptions.MissingMandatoryFieldException;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.GenericAviationBuilder;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.AddAircraftToExcel;
import uk.gov.digital.ho.egar.submission.model.GarSubmissionRequest;

@Component
public class AddAircraftToExcelImpl implements AddAircraftToExcel {
	
	@Autowired
	private GenericAviationBuilder aviationBuilder;

	public void addAircraftToExcel(XSSFRow row, GarSubmissionRequest submission) throws MissingMandatoryFieldException {
		T_GenericAviation xmlAircraft =  aviationBuilder.build(submission);
		XSSFCell cur =  row.getCell(CELL_NUM_REG);
		cur.setCellValue(xmlAircraft.getAircraftRegistrationNumber());
		cur =  row.getCell(CELL_NUM_BASE);
		cur.setCellValue(xmlAircraft.getBasedAt());
		cur =  row.getCell(CELL_NUM_TYPE);
		cur.setCellValue(xmlAircraft.getAircraftType());
	}
	
	public void addAircraftToExcelRowTwo(XSSFRow row, GarSubmissionRequest submission) throws MissingMandatoryFieldException {
		T_GenericAviation xmlAircraft =  aviationBuilder.build(submission);
		XSSFCell cur =  row.getCell(CELL_NUM_ROW_2_REASON_VISIT);
		cur.setCellValue(xmlAircraft.getReasonForVisit()==null?"":xmlAircraft.getReasonForVisit().value());
	}

}
