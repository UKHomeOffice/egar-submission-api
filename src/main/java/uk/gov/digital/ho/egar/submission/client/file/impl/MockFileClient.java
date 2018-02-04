package uk.gov.digital.ho.egar.submission.client.file.impl;

import com.fincore.cbpcarrierwebservice.entities.File;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.submission.client.file.FileClient;
import uk.gov.digital.ho.egar.submission.model.SupportingFilesSubmissionRequest;

@Component
@Profile({"s3-mocks"})
public class MockFileClient implements FileClient {

	@Override 
		  public File getFile(SupportingFilesSubmissionRequest supportingFile) { 
		 
		    byte[] bytes = null; 
		    File file = new File(); 
		    file.setFilename(supportingFile.getFileName()); 
		    file.setData(bytes);
		     
		    return file; 
		  } 
}
