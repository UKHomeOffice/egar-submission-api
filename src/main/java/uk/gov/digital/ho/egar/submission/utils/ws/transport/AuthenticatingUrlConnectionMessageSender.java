package uk.gov.digital.ho.egar.submission.utils.ws.transport;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.Assert;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

/**
 * Snipped from https://github.com/ceharris/avaya-aes-demo/
 *
 * @author Keshava.Grama
 */
public class AuthenticatingUrlConnectionMessageSender extends HttpsUrlConnectionMessageSender {

    private final String username;
    private final String password;
    private String authorization;

    /**
	 * Set to fail fast if the CBP username and password  are not set
     */
    public AuthenticatingUrlConnectionMessageSender(String username, String password) {
        super();
		Assert.notNull(username, "Username for CBP not set");
		Assert.notNull(password, "Password for CBP not set");
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void afterPropertiesSet() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("Basic ");
        sb.append(credential());
        authorization = sb.toString();
        super.afterPropertiesSet();
    }

    private String credential() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(getUsername());
        sb.append(':');
        sb.append(getPassword());
        Base64 codec = new Base64();
        return codec.encodeAsString(sb.toString().getBytes("US-ASCII"));
    }

    @Override
    protected void prepareConnection(HttpURLConnection connection)
            throws IOException {
        super.prepareConnection(connection);
        connection.setRequestProperty("Authorization", authorization);
    }

}