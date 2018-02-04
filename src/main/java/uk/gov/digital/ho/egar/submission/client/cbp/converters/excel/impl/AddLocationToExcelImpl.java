package uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.impl;

import java.util.Optional;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fincore.cbp.stt.xml.T_LatLongPair;
import com.fincore.cbp.stt.xml.T_Port;

import uk.gov.digital.ho.egar.submission.api.exceptions.MissingMandatoryFieldException;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.GeographicLocationToPortConverter;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.AddLocationToExcel;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.ExcelDateTimeFormatter;
import uk.gov.digital.ho.egar.submission.model.GeographicLocation;
import uk.gov.digital.ho.egar.submission.utils.AddressBuilderUtils;

@Component
public class AddLocationToExcelImpl implements AddLocationToExcel {

	@Autowired
	private GeographicLocationToPortConverter portConverter;
	
	@Autowired
	private ExcelDateTimeFormatter excelDateTimeFormatter;

	@Autowired
	private AddressBuilderUtils addressBuilderUtils;

	@Override
	public void addLocationToExcelRow(XSSFRow row, GeographicLocation location) throws MissingMandatoryFieldException {
		//ICAO OR IATA CODE OF ARRIVAL PORT	 <port>	DATE OF ARRIVAL <dd/mm/yyyy>		TIME OF ARRIVAL (HH:MM:SS)
		Optional<T_Port> port = portConverter.convert(location);
		if (!port.isPresent()) {
			throw new MissingMandatoryFieldException("Port not generated for location");
		}
		String excelLoc = buildExcelLocFromT_Port(port.get());
		row.getCell(1).setCellValue(excelLoc);
		// TODO: Add localdate time from location to excel as string

		row.getCell(3).setCellValue(excelDateTimeFormatter.formatDate(location.getDatetime()));
		row.getCell(5).setCellValue(excelDateTimeFormatter.formatTime(location.getDatetime()));

	}

	private String buildExcelLocFromT_Port(T_Port port) {
		if (port.getICAO() != null && !port.getICAO().isEmpty())
			return port.getICAO();
		if (port.getIATA() != null && !port.getIATA().isEmpty())
			return port.getIATA();
		if (port.getDescription() != null && !port.getDescription().isEmpty())
			return port.getDescription();
		if (port.getFullAddress() != null )
			return addressBuilderUtils.buildAddressAsString(port.getFullAddress());
		if (port.getLatLongPair()!= null ) {
			return buildPointAsString(port.getLatLongPair());
		}
		return "";
	}
	
	private static final String POINT_PREFIX = "zzzz ";
	private String buildPointAsString(T_LatLongPair latLongPair) {
		StringBuilder pointBuilder  = new StringBuilder();
		pointBuilder.append(POINT_PREFIX)
		.append(latLongPair.getLat()).append(" ").append(latLongPair.getLong());
		return pointBuilder.toString();
	}



}
