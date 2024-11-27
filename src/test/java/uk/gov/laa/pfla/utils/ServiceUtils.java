package uk.gov.laa.pfla.utils;

import java.io.IOException;
import java.net.Socket;

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

    public static String getUrl(String urlToAppend) {
        return String.format("http://%s:%d/%s", serverName, payForLegalAidPort, urlToAppend);
    }

}
