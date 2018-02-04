package uk.gov.digital.ho.egar.submission.client.cbp;

public interface CbpConstants {

    static final String CBP_WEBSERVICE_SOAP_PACKAGE_NAME = "com.fincore.cbpcarrierwebservice";
    static final String BETA_CANCELLATION_REASON = "Resubmission";
    
    static final String SOAP_ACTION_SUBMIT = "http://fincore.com/CBPCarrierWebService/ICarrierService/SubmitSTTXML";
    static final String SOAP_ACTION_CANCEL = "http://fincore.com/CBPCarrierWebService/ICarrierService/Cancel";
    static final String SOAP_ACTION_HEARTBEAT = "http://fincore.com/CBPCarrierWebService/ICarrierService/Heartbeat";
    static final String SOAP_ACTION_SUBMIT_WITH_DOCS ="http://fincore.com/CBPCarrierWebService/ICarrierService/SubmitWithAttachments";
    
    static String STT_XML_VERSION = "1.5";
    static final String DEPARTURE_CONFIRMATION_EVENT_CODE = "DC";


}