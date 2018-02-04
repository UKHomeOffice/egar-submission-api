package uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fincore.cbp.stt.xml.T_Person;
import com.fincore.cbp.stt.xml.T_TravelDocument;

import uk.gov.digital.ho.egar.submission.api.exceptions.MissingMandatoryFieldException;
import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionTransformationException;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.PersonBuilder;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.AddPeopleToExcel;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.ExcelDateTimeFormatter;
import uk.gov.digital.ho.egar.submission.model.People;
import uk.gov.digital.ho.egar.submission.model.PersonsSubmissionRequest;
import uk.gov.digital.ho.egar.submission.utils.AddressBuilderUtils;

@Component
public class AddPeopleToExcelImpl implements AddPeopleToExcel {

    public static final String GIVEN_NAME_DELIMITER = " ";
    public static final int FIRST_CELL_INDEX = 0;


    @Autowired
	private PersonBuilder personXmlBuilder;

	@Autowired
	private ExcelDateTimeFormatter dateToExcel;

	@Autowired
	private AddressBuilderUtils addressBuilderUtils;

	public void addPassengersToSheet(XSSFSheet sheet, PersonsSubmissionRequest request) throws MissingMandatoryFieldException, SubmissionTransformationException  {
		if (request==null){
			throw new MissingMandatoryFieldException("No person information on request. Captain is required.");
		}

		addCrewToSheet(sheet, request.getCrew(), request.getCaptain());
		addPassengersToSheet(sheet, request.getPassengers());
	}


	private void addPassengersToSheet(XSSFSheet sheet, List<People> pass) throws SubmissionTransformationException {
		Row passMarker = getRowWithText(sheet, TEXT_IN_CELL_FOR_PASSENGERS);

		if (passMarker == null){
		    throw new SubmissionTransformationException("Unable to locate passengers text in excel spreadsheet template");
        }

		int currentRowIndex = passMarker.getRowNum() +2;

        passMarker = getEmptyPersonRow(sheet, currentRowIndex);
        CellStyle passCell = passMarker.getCell(FIRST_CELL_INDEX).getCellStyle();

        if (pass!=null) {
            for (People curCrew : pass) {
                passMarker = getEmptyPersonRow(sheet, currentRowIndex);
                Optional<T_Person> tPerson = personXmlBuilder.builder(curCrew, true);
                if (tPerson.isPresent()) {
                    addXmlPersonToRow(tPerson.get(), passMarker, passCell);
                }

                //increment current row for next iteration
                ++currentRowIndex;
            }
        }
	}


	private void addCrewToSheet(XSSFSheet sheet,List<People> crew, People capt ) throws MissingMandatoryFieldException, SubmissionTransformationException {
		Row crewMarker = getRowWithText(sheet, TEXT_IN_CELL_FOR_CREW);

		if (crewMarker == null){
            throw new SubmissionTransformationException("Unable to locate crew text in excel spreadsheet template");
        }

		Row crewDetails = sheet.getRow(crewMarker.getRowNum()+2);

        CellStyle crewCellStyle = crewDetails.getCell(FIRST_CELL_INDEX).getCellStyle();

		Optional<T_Person> captainPerson = personXmlBuilder.builder(capt, true);
		if (!captainPerson.isPresent()) {
			throw new MissingMandatoryFieldException("Captain information is missing");
		}
		addXmlPersonToRow(captainPerson.get(), crewDetails, crewCellStyle);

		addCaptainsSurname(captainPerson.get(), sheet);

		int currentRowIndex = crewDetails.getRowNum();

		if (crew!=null) {
			for (People curCrew : crew) {
				crewDetails = getEmptyPersonRow(sheet, ++currentRowIndex);
				captainPerson = personXmlBuilder.builder(curCrew, true);
				if (captainPerson.isPresent()) {
					addXmlPersonToRow(captainPerson.get(), crewDetails, crewCellStyle);
				}
				currentRowIndex = crewDetails.getRowNum();
			}
		}
	}

    private void addCaptainsSurname(T_Person captain, XSSFSheet sheet) {
        XSSFRow row = sheet.getRow(CAPTAIN_SURNAME_ROW_INDEX);
        row.getCell(CAPTAIN_SURNAME_CELL_INDEX).setCellValue(captain.getSurname());
    }

    private Row getEmptyPersonRow(XSSFSheet sheet, int row_index) {
        Row nextRow = sheet.getRow(row_index+1);
        if (!isRowEmpty(nextRow, 12)){
            sheet.shiftRows(row_index, sheet.getLastRowNum(), 1, true, false);
            sheet.createRow(row_index);
        }

	    return sheet.getRow(row_index);
	}

    @SuppressWarnings("unused")
	private boolean isRowEmpty(Row row) {
        return isRowEmpty(row,row.getLastCellNum());
    }

    private boolean isRowEmpty(Row row, int lastCell) {
        for (int c = row.getFirstCellNum(); c < lastCell; c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellTypeEnum() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds the xml person to the row sent in.
     * @param person The xml person to add
     * @param personRow The person row.
     */
	private void addXmlPersonToRow(final T_Person person, final Row personRow, CellStyle personCellStyle) {

		T_TravelDocument tdoc = person.getTravelDocument().get(0);

		getOrCreateCell(personRow, TRAVEL_DOCUMENT_TYPE_CELL_INDEX, personCellStyle).setCellValue(tdoc.getDocumentType().value());

        getOrCreateCell(personRow, NATURE_OF_ID_DOCUMENT_CELL_INDEX, personCellStyle);

        getOrCreateCell(personRow, TRAVEL_DOCUMENT_ISSUING_COUNTRY_CELL_INDEX, personCellStyle).setCellValue(tdoc.getIssuingAuthority());

        getOrCreateCell(personRow, TRAVEL_DOCUMENT_NUMBER_CELL_INDEX, personCellStyle).setCellValue(tdoc.getDocumentNumber());

        getOrCreateCell(personRow, SURNAME_CELL_INDEX, personCellStyle).setCellValue(person.getSurname());


        StringBuilder nameBuilder = new StringBuilder();
		for (String names:person.getGivenName()) {
			nameBuilder.append(names).append(GIVEN_NAME_DELIMITER);
		}

        getOrCreateCell(personRow, FORENAME_CELL_INDEX,personCellStyle).setCellValue(nameBuilder.toString());

        getOrCreateCell(personRow, GENDER_CELL_INDEX,personCellStyle).setCellValue(person.getGender().name());

        getOrCreateCell(personRow, DOB_CELL_INDEX, personCellStyle).setCellValue(dateToExcel.formatXmlDate(person.getDateOfbirth()));

        getOrCreateCell(personRow, PLACE_OF_BIRTH_CELL_INDEX, personCellStyle).setCellValue(person.getPlaceOfBirth());

        getOrCreateCell(personRow, NATIONALITY_CELL_INDEX, personCellStyle).setCellValue(person.getNationality());

        getOrCreateCell(personRow, TRAVEL_DOCUMENT_EXPIRY_DATE_CELL_INDEX, personCellStyle).setCellValue(dateToExcel.formatXmlDate(tdoc.getExpirationDate()));

		if (person.getHomeAddress()!=null) {
            getOrCreateCell(personRow, HOME_ADDRESS_CELL_INDEX, personCellStyle).setCellValue(addressBuilderUtils.buildAddressAsString(person.getHomeAddress()));
        }
	}

    /**
     * Retrieve or creates a cell
     * @param row The row.
     * @param index The index
     * @return The cell.
     */
	private Cell getOrCreateCell(final Row row, final int index, final CellStyle cellStyle){
        Cell cell = row.getCell(index);
        if (cell==null){
            cell = row.createCell(index);
        }
        cell.setCellStyle(cellStyle);
        return cell;
    }


    /**
     * Finds the row with text in the first cell.
     * @param sheet The sheet
     * @param text The text to find.
     * @return The row.
     */
	private Row getRowWithText(final XSSFSheet sheet, final String text) {
		for (Row row : sheet) {
			Cell cell  =  row.getCell(FIRST_CELL_INDEX);
			if (cell!=null && cell.getStringCellValue().toUpperCase().equals(text.toUpperCase())) {
				return row;
			}
		}
		return null;
	}
}
