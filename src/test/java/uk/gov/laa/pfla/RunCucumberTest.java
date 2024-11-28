package uk.gov.laa.pfla;

import io.cucumber.java.Before;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

import java.io.IOException;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("uk")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
//The below allows us to just spin up the service once and not for every test
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RunCucumberTest {
    private ServiceManager serviceManager;

    // This is a Junit BeforeAll.
    // Note there is a Cucumber BeforeAll we can also use simultaneously if there is Cucumber Specific Setup needed
    @BeforeAll
    public static void setup() throws IOException {
        System.out.println("CCCCCCCCC *** before");

        //serviceManager = new ServiceManager();
        ServiceManager.startService();
    }

    @AfterAll
    public void teardown() {
        System.out.println("CCCCCCCCC *** afterAll");

        ServiceManager.stopService();
    }
}
