package uk.gov.digital.ho.egar.submission.client.cbp.soap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

import com.fincore.cbpcarrierwebservice.Cancel;
import com.fincore.cbpcarrierwebservice.CancelResponse;
import com.fincore.cbpcarrierwebservice.Heartbeat;
import com.fincore.cbpcarrierwebservice.HeartbeatResponse;
import com.fincore.cbpcarrierwebservice.ObjectFactory;
import com.fincore.cbpcarrierwebservice.SubmitSTTXML;
import com.fincore.cbpcarrierwebservice.SubmitSTTXMLResponse;
import com.fincore.cbpcarrierwebservice.SubmitWithAttachments;
import com.fincore.cbpcarrierwebservice.SubmitWithAttachmentsResponse;
import com.fincore.cbpcarrierwebservice.entities.ArrayOfFile;
import com.fincore.cbpcarrierwebservice.entities.CancelReturnType;
import com.fincore.cbpcarrierwebservice.entities.SubmitReturnType;

import uk.gov.digital.ho.egar.submission.client.cbp.CbpClient;
import uk.gov.digital.ho.egar.submission.client.cbp.CbpConstants;
import uk.gov.digital.ho.egar.submission.model.service.CbpSubmissionResponse;

public class WebServiceCbpClient extends WebServiceGatewaySupport implements CbpClient{
	private static final Log logger = LogFactory.getLog(WebServiceCbpClient.class);
	
	private final HttpsUrlConnectionMessageSender msgSender;
	
	private final String username;
	private final String password;
	private final ObjectFactory objFac; 
	private final com.fincore.cbpcarrierwebservice.entities.ObjectFactory entityFactory;

	/**
	 * Set to fail fast if the CBP username, password and url are not set
	 */
	public WebServiceCbpClient (String username, String password, String defaultUri, 
									ObjectFactory objFac,
									HttpsUrlConnectionMessageSender msgSender) {
		super();
		Assert.notNull(username, "Username for CBP not set");
		Assert.notNull(password, "Password for CBP not set");
		Assert.notNull(defaultUri, "URL for CBP not set");
		this.username = username;
		this.password = password;
		setDefaultUri(defaultUri);
		this.objFac = objFac;
		this.msgSender = msgSender ;
		this.entityFactory = new com.fincore.cbpcarrierwebservice.entities.ObjectFactory();
		logger.info("CBP URI set to:");
		logger.info(defaultUri); 
	}
	
	
	public CbpSubmissionResponse submitSTTXML(String sttXmlAsString) {
		logger.info("submitting to CBP"); 
		getWebServiceTemplate().setMessageSender(msgSender);
		SubmitSTTXML submission = objFac.createSubmitSTTXML();
		submission.setUsername(objFac.createSubmitSTTXMLUsername(username));
		submission.setPassword(objFac.createSubmitSTTXMLPassword(password));
		submission.setSttXml(objFac.createSubmitSTTXMLSttXml(sttXmlAsString));
		SubmitSTTXMLResponse response = (SubmitSTTXMLResponse) getWebServiceTemplate()
				.marshalSendAndReceive(submission, new SoapActionCallback(CbpConstants.SOAP_ACTION_SUBMIT));

        SubmitReturnType responseType = response.getSubmitSTTXMLResult().getValue();

        CbpSubmissionResponse cbpResponse = CbpSubmissionResponse.builder()
                .success(responseType.isOK())
                .reason(responseType.getReason().getValue())
                .identifier(responseType.getIdentifier().getValue())
                .build();

		return cbpResponse;
	}

	@Override
	public CbpSubmissionResponse cancelSubmittedGar(String externalSubmissionRef) {
		logger.info("cancelling submitted GAR: "+ externalSubmissionRef); 
		getWebServiceTemplate().setMessageSender(msgSender);
		Cancel cancellation = objFac.createCancel();
		cancellation.setUsername(objFac.createCancelUsername(username));
		cancellation.setPassword(objFac.createCancelPassword(password));
		cancellation.setIdentifier(objFac.createCancelIdentifier(externalSubmissionRef));
		cancellation.setReason(objFac.createCancelReason(CbpConstants.BETA_CANCELLATION_REASON));
		CancelResponse response = (CancelResponse) getWebServiceTemplate()
				.marshalSendAndReceive(cancellation, new SoapActionCallback(CbpConstants.SOAP_ACTION_CANCEL)); 
		CancelReturnType retType = response.getCancelResult().getValue();

		CbpSubmissionResponse cbpResponse = CbpSubmissionResponse.builder()
				.success(retType.isOK())
				.reason(retType.getReason().getValue())
				.identifier(externalSubmissionRef)
				.build();

		return cbpResponse;
	}


	@Override
	public boolean isCBPServiceAvailable() {
		logger.info("Checking CBP heartbeat"); 
		getWebServiceTemplate().setMessageSender(msgSender);
		Heartbeat hearbeat = objFac.createHeartbeat();
		hearbeat.setUsername(objFac.createHeartbeatUsername(username));
		hearbeat.setPassword(objFac.createHeartbeatPassword(password));
		HeartbeatResponse response = (HeartbeatResponse) getWebServiceTemplate()
				.marshalSendAndReceive(hearbeat, new SoapActionCallback(CbpConstants.SOAP_ACTION_HEARTBEAT)); 
		return response.isHeartbeatResult();
	}


	@Override
	public CbpSubmissionResponse submitWithAttachments(ArrayOfFile submissionFiles) {
		logger.info(String.format("Submitting Gar with attachments, number of files including GAR %d", submissionFiles.getFile().size()));
		SubmitWithAttachments request = objFac.createSubmitWithAttachments();
		request.setUsername(objFac.createSubmitWithAttachmentsUsername(username));
		request.setPassword(objFac.createSubmitWithAttachmentsPassword(password));
		request.setFiles(objFac.createSubmitWithAttachmentsFiles(submissionFiles));
		SubmitWithAttachmentsResponse responseType = (SubmitWithAttachmentsResponse)
				getWebServiceTemplate().marshalSendAndReceive(request, new SoapActionCallback(CbpConstants.SOAP_ACTION_SUBMIT_WITH_DOCS));
		SubmitReturnType ret = responseType.getSubmitWithAttachmentsResult().getValue();
		CbpSubmissionResponse cbpResponse = CbpSubmissionResponse.builder()
                .success(ret.isOK())
                .reason(ret.getReason().getValue())
                .identifier(ret.getIdentifier().getValue())
                .build();

		return cbpResponse;
	}


	
}
