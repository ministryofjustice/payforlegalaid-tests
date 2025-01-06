package uk.gov.laa.pfla.utils;

import io.restassured.response.Response;

import java.io.IOException;
import java.net.Socket;

import static io.restassured.RestAssured.given;

public final class ServiceUtils {

    public static final int payForLegalAidPort = 8080;
    public static final String serverName = "localhost";

    public static void checkLocalServiceIsRunning() throws InterruptedException {
        int timeout = 30; // Seconds
        while (timeout > 0) {
            try (Socket socket = new Socket(serverName, payForLegalAidPort)) {
                //Service is ready to be called
                return;
            } catch (IOException e) {
                Thread.sleep(1000);
                timeout--;
            }
        }
        throw new RuntimeException("Service did not start in time");
    }

    public static void checkServiceIsRunning() {
        //TODO not fit for purpose as 200 interferes with /reports response
//        Response response = makeGetCall("health", System.getProperty("BASE_URL"));
//        assertEquals(200, response.getStatusCode(), "Expected 200 OK response but received " + response.getStatusCode());
    }

    public static Response makeGetCall(String endpoint, String baseUrl) {
        return given().baseUri(baseUrl).redirects().follow(false)
                .get(endpoint);
    }

    public static Response makeGetCall(String endpoint, String parameter, String baseUrl, String token) {
        if (token != null) {
            return makeGetCall(String.format("%s/%s", endpoint, parameter), baseUrl);
        } else {
            return makeGetCallWithToken(String.format("%s/%s", endpoint, parameter), baseUrl,token);
        }
    }

    public static Response makeGetCallWithToken(String endpoint, String baseUrl, String token) {
        System.out.println("TOKEN: " +token);
        return given().baseUri(baseUrl).headers("Authorization", token).redirects().follow(false)
                .get(endpoint);
    }

}
