package uk.gov.laa.pfla;

import java.io.File;
import java.io.IOException;

public class ServiceManager {
    private static Process serviceProcess;

    public static void startService() throws IOException {
        String jarPath = new File("target/libs/pay-for-legal-aid-0.0.1-SNAPSHOT.jar").getAbsolutePath();
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "-Dspring.profiles.active=local", jarPath);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        serviceProcess = processBuilder.start();
    }

    public static void stopService() {
        if (serviceProcess != null && serviceProcess.isAlive()) {
            serviceProcess.destroyForcibly();
        }
    }

}
