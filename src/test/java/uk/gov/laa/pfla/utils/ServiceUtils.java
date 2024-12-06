package uk.gov.laa.pfla.utils;

import io.restassured.response.Response;

import java.io.IOException;
import java.net.Socket;

import static io.restassured.RestAssured.given;

public final class ServiceUtils {

    public static final int payForLegalAidPort = 8080;
    public static final String serverName = "localhost";

    public static void checkServiceIsRunning() throws InterruptedException {
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

    public static Response makeGetCall(String endpoint) {
        return given().baseUri(String.format("http://%s:%d/", serverName, payForLegalAidPort)).get(endpoint);
    }

    public static Response makeGetCall(String endpoint, String parameter) {
        return makeGetCall(String.format("%s/%s", endpoint, parameter));
    }

}
