package uk.gov.digital.ho.egar.submission.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import uk.gov.digital.ho.egar.submission.client.cbp.CbpConstants;

@Configuration
public class Jaxb2Config {


    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(CbpConstants.CBP_WEBSERVICE_SOAP_PACKAGE_NAME);
        return marshaller;
    }
}
