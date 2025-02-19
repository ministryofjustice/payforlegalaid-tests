package uk.gov.laa.pfla;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.gov.laa.gpfd.Application;
//import uk.gov.laa.gpfd.Application;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("uk")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@SpringBootApplication
public class RunCucumberTest {

//    public static void main(String[] args) {
//        Application.main(args);
//
//    }
}
