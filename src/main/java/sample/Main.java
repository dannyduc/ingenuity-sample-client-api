package sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author <a href="mailto:dnguyen@ingenuity.com">Danny Nguyen</a>
 *
 * Sample program to upload isa tab formatted files to Ingenuity.
 *
 */
public class Main {

    static Client client;
    static {
        DefaultApacheHttpClientConfig cc = new DefaultApacheHttpClientConfig();
        if (Config.PROXY_URI != null) {
            cc.getProperties().put(DefaultApacheHttpClientConfig.PROPERTY_PROXY_URI, Config.PROXY_URI);
        }
        client = ApacheHttpClient.create(cc);
    }

    static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws InterruptedException, IOException {

        String accessToken = getAccessToken();

        String statusUrl = upload(accessToken);

        while (!isDone(statusUrl, accessToken)) {
            Thread.sleep(5000);
        }

        String dataPackageId = parseDataPackageId(statusUrl);
        String exportUri = Config.EXPORT_URI + "/" + dataPackageId;

        export(exportUri, accessToken);
    }

    private static String parseDataPackageId(String statusUrl) {
        int start = statusUrl.lastIndexOf('/');
        if (start == -1) {
            throw new RuntimeException("Unable to parse dataPackageId from statusUrl: " + statusUrl);
        }
        String dataPackageId = statusUrl.substring(start+1);

        System.out.println("dataPackageId: " + dataPackageId);

        return dataPackageId;
    }

    public static String getAccessToken() throws IOException {

        String json = client
                .resource(Config.ACCESS_TOKEN_URI
                        + "?grant_type=client_credential"
                        + "s&client_id=" + Config.CLIENT_ID
                        + "&client_secret=" + Config.CLIENT_SECRET)
                .post(String.class);

        String token = parseJson(json, "access_token");

        System.out.println("accessToken: " + token);

        return token;
    }

    private static String upload(String accessToken) throws IOException {
        FormDataMultiPart multiPart = new FormDataMultiPart();

        // replace with your actual experiment files
        // use StreamDataBodyPart if prefer not to use a file

        multiPart.bodyPart(new FileDataBodyPart("file", new File(Config.SAMPLE_FILE)));

        ClientResponse clientResponse = client.resource(Config.API_URI)
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)
                .header("Authorization", accessToken)
                .post(ClientResponse.class, multiPart);

        String json = clientResponse.getEntity(String.class);

        String statusUrl = parseJson(json, "status-url");

        System.out.println("statusUrl: " + statusUrl);

        return statusUrl;
    }

    private static boolean isDone(String statusUri, String accessToken) throws IOException {
        String json = client
                .resource(statusUri)
                .header("Authorization", accessToken)
                .get(String.class);

        String status = parseJson(json, "status");

        System.out.println("status: " + status);

        return "DONE".equals(status);
    }

    private static String parseJson(String json, String fieldName) throws IOException {
        Map m = mapper.readValue(json, Map.class);
        return (String) m.get(fieldName);
    }


    private static void export(String exportUri, String accessToken) throws IOException {
        InputStream input = client
                .resource(exportUri)
                .header("Authorization", accessToken)
                .get(InputStream.class);

        File file = new File("exportedFile.vcf");
        FileOutputStream output = new FileOutputStream(file);
        IOUtils.copy(input, output);

        IOUtils.closeQuietly(output);
        IOUtils.closeQuietly(input);

        System.out.println("Exported file to: " + file.getAbsolutePath());
    }
}
