package uk.gov.digital.ho.egar.submission.config;


import uk.gov.digital.ho.egar.submission.client.cbp.CbpClient;

/**
 * Used to ensure the Prod & mock configs are the same.
 */
public interface CbpClientConfig {

	/**
	 * used to create the CbpClient object bean.
	 * @return
	 */
	CbpClient cbpClient();

}