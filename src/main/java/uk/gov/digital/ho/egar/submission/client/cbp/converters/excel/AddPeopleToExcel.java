package uk.gov.digital.ho.egar.submission.client.cbp.converters.excel;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import uk.gov.digital.ho.egar.submission.api.exceptions.MissingMandatoryFieldException;
import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionTransformationException;
import uk.gov.digital.ho.egar.submission.model.PersonsSubmissionRequest;

public interface AddPeopleToExcel {
	public static String TEXT_IN_CELL_FOR_CREW = "CREW";
	public static String TEXT_IN_CELL_FOR_PASSENGERS = "PASSENGERS";

	int TRAVEL_DOCUMENT_TYPE_CELL_INDEX = 0;
	int NATURE_OF_ID_DOCUMENT_CELL_INDEX = 1;
	int TRAVEL_DOCUMENT_ISSUING_COUNTRY_CELL_INDEX = 2;
	int TRAVEL_DOCUMENT_NUMBER_CELL_INDEX = 3;
	int SURNAME_CELL_INDEX = 4;
	int FORENAME_CELL_INDEX = 5;
	int GENDER_CELL_INDEX = 6;
	int DOB_CELL_INDEX = 7;
	int PLACE_OF_BIRTH_CELL_INDEX = 8;
	int NATIONALITY_CELL_INDEX = 9;
	int TRAVEL_DOCUMENT_EXPIRY_DATE_CELL_INDEX = 10;
	int HOME_ADDRESS_CELL_INDEX = 11;

	int CAPTAIN_SURNAME_ROW_INDEX = 4;
	int CAPTAIN_SURNAME_CELL_INDEX = 5;

	public void addPassengersToSheet(XSSFSheet sheet, PersonsSubmissionRequest request) throws MissingMandatoryFieldException, SubmissionTransformationException;
}
