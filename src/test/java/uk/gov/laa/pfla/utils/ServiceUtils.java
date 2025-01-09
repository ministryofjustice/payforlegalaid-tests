package uk.gov.laa.pfla.utils;

import io.restassured.response.Response;
import jakarta.servlet.http.Cookie;

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

    public static Response checkServiceIsRunning() {
        return makeGetCall("actuator", System.getProperty("BASE_URL"));
    }

    public static Response makeGetCall(String endpoint, String baseUrl) {
        return given().baseUri(baseUrl).redirects().follow(false)
                .get(endpoint);
    }
//
//    public static Response makeGetCall(String endpoint, String parameter, String baseUrl) {
//            return makeGetCall(String.format("%s/%s", endpoint, parameter), baseUrl);
//    }

    public static Response makeGetCall(String endpoint, String baseUrl, Cookie cookie) {
        return given().baseUri(baseUrl).redirects().follow(false).headers("cookie", cookie.getName() + "=" + cookie.getValue())
                .get(endpoint);
    }

}
