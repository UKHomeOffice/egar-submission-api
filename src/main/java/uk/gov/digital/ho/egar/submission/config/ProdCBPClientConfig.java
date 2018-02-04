package uk.gov.digital.ho.egar.submission.config;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

import com.fincore.cbpcarrierwebservice.ObjectFactory;

import uk.gov.digital.ho.egar.submission.client.cbp.CbpClient;
import uk.gov.digital.ho.egar.submission.client.cbp.soap.WebServiceCbpClient;
import uk.gov.digital.ho.egar.submission.utils.ws.transport.AuthenticatingUrlConnectionMessageSender;

/**
 * Config for the CBP ws client for production.
 * 
 * Construction of {@link #connectionMessageSender()} and {@link #cbpClient()} will fail fast if the CBP username, password and url are not set.
 * 
 * if using the soap mock for CBP URL to use:
 * "http://localhost:9046/CarrierWebService/CarrierService.svc"
 *
 * @author Keshava.Grama
 */
@Configuration
@Profile("!cbp-mocks")
public class ProdCBPClientConfig implements CbpClientConfig {

	@Autowired
	private CbpConfig config ;
	
	@Autowired
	private Jaxb2Marshaller marshal ;

	/**
	 * Message sender for Https connections
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 */
    @Bean
    @Primary
    public HttpsUrlConnectionMessageSender connectionMessageSender() throws NoSuchAlgorithmException, KeyStoreException {
        AuthenticatingUrlConnectionMessageSender retVal = new AuthenticatingUrlConnectionMessageSender(
        		config.getUserNameKey(),
        		config.getUserPasswordKey());
        
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        // Using null here initialises the TMF with the default trust store.
        tmf.init((KeyStore) null);
        retVal.setTrustManagers(tmf.getTrustManagers());
        
        // Don't worry about SSL certs on a local machine
        retVal.setHostnameVerifier((hostname, sslSession) -> {
            if (hostname.equals("localhost")) 
            {
                return true;
            }
            return false;
        });
        return retVal;
    }
    
	
    @Bean
    @Override
    public CbpClient cbpClient() 
    {
        WebServiceCbpClient retVal;
		try {
			ObjectFactory objFac = new ObjectFactory();
			retVal = new WebServiceCbpClient(
					config.getUserNameKey(),
					config.getUserPasswordKey(),
					config.getUserUrlKey(),
			        objFac,
			        connectionMessageSender()); 
			retVal.setMarshaller(marshal);
			retVal.setUnmarshaller(marshal);
		} catch (NoSuchAlgorithmException|KeyStoreException ex) {
			throw new RuntimeException("Unable to create cbpClient",ex);
		}
        
        return retVal;
    }

}
