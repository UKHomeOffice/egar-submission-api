package uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.impl;

import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fincore.cbpcarrierwebservice.entities.ArrayOfFile;
import com.fincore.cbpcarrierwebservice.entities.File;
import com.fincore.cbpcarrierwebservice.entities.ObjectFactory;

import uk.gov.digital.ho.egar.submission.api.exceptions.SubmissionApiException;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.GarFileMarshaller;
import uk.gov.digital.ho.egar.submission.client.cbp.converters.excel.ExcelFileBuilder;
import uk.gov.digital.ho.egar.submission.client.file.FileClient;
import uk.gov.digital.ho.egar.submission.model.QueuedGarSubmissionToCbp;
import uk.gov.digital.ho.egar.submission.model.SupportingFilesSubmissionRequest;

@Component
public class GarFileMarshallerImpl implements GarFileMarshaller {
	
	private ObjectFactory entityFactory = new ObjectFactory();
	
	@Autowired
    private FileClient fileClient;
	
	@Autowired
	private ExcelFileBuilder excelFileBuilder;
	
	public ArrayOfFile convertPojoToExcel(QueuedGarSubmissionToCbp submission) throws IOException, SubmissionApiException, InvalidFormatException{
		ArrayOfFile returnVal = entityFactory.createArrayOfFile();
		List<File> cbpFileList =  returnVal.getFile();
		cbpFileList.add(excelFileBuilder.convertPojoToExcel(submission));


		//Loop through the files in submission object.
		List<SupportingFilesSubmissionRequest> supportingFiles = submission.getSupportingFiles();
		for(SupportingFilesSubmissionRequest supportingFile: supportingFiles){
			
			// Retrieve file from client
			File file = fileClient.getFile(supportingFile);
			
			// Add file to cbpFileList
			cbpFileList.add(file);
			
		}
		return returnVal;
	}

}
