package uk.gov.laa.pfla.configuration;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.client.RestTemplate;
import uk.gov.laa.gpfd.graph.GraphClient;
import uk.gov.laa.gpfd.graph.StubbedGraphClient;
import uk.gov.laa.pfla.client.RestTemplateWithErrorHandling;
import uk.gov.laa.pfla.client.AuthenticationProvider;
import uk.gov.laa.pfla.client.RestClient;
import uk.gov.laa.pfla.client.interceptor.AuthenticationInterceptor;
import uk.gov.laa.pfla.client.interceptor.HostInterceptor;
import uk.gov.laa.pfla.comparator.WorkbookComparator;
import uk.gov.laa.pfla.scenario.ScenarioContext;
import uk.gov.laa.pfla.service.HttpProvider;
import uk.gov.laa.pfla.util.WorkbookUtil;

import java.util.function.Supplier;

import static java.util.List.of;
import static uk.gov.laa.pfla.client.AuthenticationProvider.basicAuth;
import static uk.gov.laa.pfla.client.interceptor.HostInterceptor.withHost;


@Configuration
public class TestConfig {

    @Bean
    @Primary
    public GraphClient graphClient() {
        return new StubbedGraphClient();
    }

    @Bean
    @Qualifier("validAuthenticationInterceptor")
    public AuthenticationInterceptor authenticationInterceptor() {
        return this::authenticationProvider;
    }

    @Bean
    public HostInterceptor hostInterceptor() {
        return withHost("http://localhost:8080");
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return basicAuth("test-user", "test-password");
    }

    @Bean
    @Qualifier("authenticatedRestTemplate")
    public RestTemplateWithErrorHandling authenticatedRestTemplate(
            @Qualifier("validAuthenticationInterceptor") AuthenticationInterceptor authenticationInterceptor,
            HostInterceptor hostInterceptor) {
        return new RestTemplateWithErrorHandling() {{
            setInterceptors(of(authenticationInterceptor, hostInterceptor));
        }};
    }

    @Bean
    @Qualifier("invalidCredentialsRestClient")
    public RestTemplateWithErrorHandling invalidCredentialsRestClient(HostInterceptor hostInterceptor) {
        return new RestTemplateWithErrorHandling() {{
            setInterceptors(of((AuthenticationInterceptor) () -> basicAuth("foo", "bar"), hostInterceptor));
        }};
    }

    @Bean
    public Supplier<RestTemplate> restTemplateSupplier() {
        return RestTemplate::new;
    }

    @Bean
    public HttpProvider httpClientServiceSupplier(RestClient restClient, ScenarioContext scenarioContext) {
        return new HttpProvider() {
            @Override
            public RestTemplate getClient() {
                return getClient(restClient, scenarioContext.getAuthenticationState());
            }
        };
    }

    @Bean
    public RestClient restClient(
            RestTemplate restTemplate,
            @Qualifier("authenticatedRestTemplate") RestTemplateWithErrorHandling restTemplateWithErrorHandling,
            @Qualifier("invalidCredentialsRestClient") RestTemplateWithErrorHandling invalidCredentialsRestClient
    ) {
        return new RestClient() {
            @Override
            public RestTemplate authenticated() {
                return restTemplateWithErrorHandling;
            }

            @Override
            public RestTemplate invalidCredentials() {
                return invalidCredentialsRestClient;
            }

            @Override
            public RestTemplate unauthenticated() {
                return restTemplate;
            }
        };
    }

    @Bean
    public ClientRegistrationRepository emptyClientRegistrationRepository() {
        var localRegistration = ClientRegistration.withRegistrationId("graph")
                .clientId("mockClientId")
                .clientSecret("mockClientSecret")
                .scope("read")
                .authorizationUri("test")
                .redirectUri("test2")
                .tokenUri("test3")
                .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
                .build();

        return new InMemoryClientRegistrationRepository(of(localRegistration));
    }

    @Bean
    public WorkbookComparator workbookComparator() {
        return new WorkbookComparator() {};
    }

    @Bean
    public WorkbookUtil testingExcelService(ScenarioContext scenarioContext) {
        return new WorkbookUtil() {
            @Override
            public Workbook getExcelWorkbook() {
                return getExcelWorkbook(scenarioContext);
            }
        };
    }
}
