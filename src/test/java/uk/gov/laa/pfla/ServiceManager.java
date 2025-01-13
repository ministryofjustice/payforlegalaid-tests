package uk.gov.laa.pfla;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessHandle;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServiceManager {
    private static Process serviceProcess;

    public static void startService() throws IOException {
//        String jarPath = new File("target/libs/pay-for-legal-aid-0.0.1-SNAPSHOT.jar").getAbsolutePath();
//        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "-Dspring.profiles.active=local", jarPath);
//        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
//        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
//        serviceProcess = processBuilder.start();
//        log.info("Pay For Legal Aid service has started");
//
//        // Attempt to stop the service running even if the JVM unexpectedly exits
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            serviceProcess.destroyForcibly();
//            log.info("Pay For Legal Aid service has been stopped due to unexpected shutdown");
//        }));
    }

    public static void stopService() throws InterruptedException {
        if (serviceProcess != null && serviceProcess.isAlive()) {
            serviceProcess.destroyForcibly();
            System.out.println("Method termination: " + serviceProcess.waitFor(1000, TimeUnit.NANOSECONDS));
            serviceProcess.exitValue();



            log.info("Pay For Legal Aid service has stopped");
            System.out.println("IS IT ALIVE: " + serviceProcess.isAlive());
//            serviceProcess.destroyForcibly().wait();
        }
    }

}
