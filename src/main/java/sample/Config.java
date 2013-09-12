package sample;

import org.apache.commons.lang.StringUtils;

import java.util.ResourceBundle;

/**
 * @author <a href="mailto:dnguyen@ingenuity.com">Danny Nguyen</a>
 *
 * Read configuration settings from config.properties file
 *
 */
public class Config {

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

    public static final String ACCESS_TOKEN_URI = resourceBundle.getString("accessTokenUri");

    public static final String API_URI = resourceBundle.getString("apiUri");

    public static final String EXPORT_URI = resourceBundle.getString("exportUri");

    public static final String PROXY_URI = StringUtils.isBlank(resourceBundle.getString("proxyUri")) ? null : resourceBundle.getString("proxyUri");

    public static final String CLIENT_ID = resourceBundle.getString("clientId");

    public static final String CLIENT_SECRET = resourceBundle.getString("clientSecret");

    public static final String SAMPLE_FILE = resourceBundle.getString("sampleFile");
}
