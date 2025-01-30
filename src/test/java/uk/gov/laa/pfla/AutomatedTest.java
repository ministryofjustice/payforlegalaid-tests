package uk.gov.laa.pfla;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class AutomatedTest {
    public static void main(String[] args) {
        try {
            String accessToken = StepDefinitions.AzureAuth.getAccessToken();

            String sessionCookie = CookieHelper.getSessionCookie(accessToken);

            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("https://yourapplication.com/api/protected-resource");

            httpGet.setHeader("Cookie", sessionCookie);

            HttpResponse response = httpClient.execute(httpGet);
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("Response: " + responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
