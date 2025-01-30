package uk.gov.laa.pfla;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CookieHelper {
    public static String getSessionCookie(String accessToken) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://localhost.com/reports");
        httpPost.setHeader("Authorization", "Bearer " + accessToken);
        httpPost.setHeader("Content-Type", "application/json");

        HttpResponse response = httpClient.execute(httpPost);
        Header[] headers = response.getHeaders("Set-Cookie");

        if (headers != null) {
            return headers[0].toString();
        }

        throw new RuntimeException("No session cookie found in the response.");
    }
}