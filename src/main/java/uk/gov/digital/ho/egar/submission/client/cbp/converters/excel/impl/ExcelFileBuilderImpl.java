package uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fincore.cbpcarrierwebservice.entities.File;

import uk.gov.digital.ho.egar.submission.api.exceptions.MissingMandatoryFieldException;
import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionApiException;
import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionTransformationException;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.AddAircraftToExcel;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.AddLocationToExcel;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.AddPeopleToExcel;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.ExcelFileBuilder;
import uk.gov.digital.ho.egar.submission.model.GeographicLocation;
import uk.gov.digital.ho.egar.submission.model.QueuedGarSubmissionToCbp;
/**
 * Uses Apache POI to read the template file from resources folder
 * This template file is then updated with the data from the GAR submission
 * Template file is read-only and isnt modified.
 * @author Keshava.Grama
 *
 */
@Component
public class ExcelFileBuilderImpl implements ExcelFileBuilder {

	public static final String FILE_NAME_FORMAT = "%s.xlsx";
	protected final Log logger = LogFactory.getLog(ExcelFileBuilderImpl.class);

	/**
	 * The filesystem of the excel template.
	 */
	private final java.io.File fs;

	@Autowired 
	private AddLocationToExcel portExcel;
	
	@Autowired 
	private AddAircraftToExcel aircraftExcel;
	
	@Autowired
	private AddPeopleToExcel peopleExcel;

	public ExcelFileBuilderImpl() throws IOException, EncryptedDocumentException, InvalidFormatException, URISyntaxException{
		URL templateStream =  Thread.currentThread().getContextClassLoader().getResource("excel-template/GAR_Template_V7_2017.xlsx");
		fs = new java.io.File(templateStream.toURI());
	}

	public File convertPojoToExcel(QueuedGarSubmissionToCbp submission) throws SubmissionApiException, InvalidFormatException {

			try {
				XSSFWorkbook templateFile = getNewTemplateFile();

				//Adding all of the gar information to the workbook
				addGarDataToTempFile(templateFile, submission);

				//Creates file object from the workbook
				return createFile(submission.getGarUuid(), templateFile);
			}catch (IOException exception){
				logger.error("Unable to read or write submission excel template", exception);
				throw new SubmissionTransformationException("Unable to read or write submission excel template.", exception);
			}

	}

	/**
	 * Create a file for the workbook.
	 * @param garUuid The gar uuid (used in the filename).
	 * @param templateFile The template file to be encoded
	 * @return The file object
	 * @throws IOException Is thrown when there is processing errors.
	 */
	private File createFile(UUID garUuid, XSSFWorkbook templateFile) throws IOException {
		File file = new File();

		String submissionFileName = String.format(FILE_NAME_FORMAT,garUuid.toString());
		file.setFilename(submissionFileName);

		//Write the template to a byte array
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		templateFile.write(baos);

		file.setData(baos.toByteArray());

		return file;
	}

	private void addGarDataToTempFile(XSSFWorkbook templateFile2, QueuedGarSubmissionToCbp submission) throws MissingMandatoryFieldException, SubmissionTransformationException {
		XSSFSheet garSheet = templateFile2.getSheet(NAME_OF_SHEET_FOR_GAR);
		addLocationsToGarExcel(garSheet, submission.getLocations());

		XSSFRow aircraftRow = garSheet.getRow(AIRCRAFT_DETAILS_ROW_INDEX);
		aircraftExcel.addAircraftToExcel(aircraftRow, submission);

		XSSFRow aircraftRowTwo = garSheet.getRow(AIRCRAFT_DETAILS_ROW_INDEX +1 );
		aircraftExcel.addAircraftToExcelRowTwo(aircraftRowTwo, submission);

		peopleExcel.addPassengersToSheet(garSheet, submission.getPersons());
	}

	private void addLocationsToGarExcel(XSSFSheet garSheet, List<GeographicLocation> locations) throws MissingMandatoryFieldException {
		XSSFRow arrivalRow = garSheet.getRow(ARRIVAL_ROW_INDEX);
		portExcel.addLocationToExcelRow(arrivalRow, locations.get(0));
		XSSFRow departureRow = garSheet.getRow(DEPARTURE_ROW_INDEX);
		portExcel.addLocationToExcelRow(departureRow, locations.get(1));
	}
	

	//Gets a new HSSFWorkbook from the filesystem.
	private XSSFWorkbook getNewTemplateFile() throws IOException, InvalidFormatException {
		return  new XSSFWorkbook(fs);
	}

}
