package uk.gov.laa.pfla;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.microsoft.aad.msal4j.*;
//import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.kiota.authentication.AzureIdentityAuthenticationProvider;
import lombok.SneakyThrows;
import okhttp3.Request;
import org.json.JSONObject;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.microsoft.kiota.authentication.AzureIdentityAuthenticationProvider;
import com.microsoft.kiota.bundle.DefaultRequestAdapter;

import java.util.concurrent.CompletableFuture;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class StepDefinitions {
    private Response response;
    private Cookie cookie;
    public static final int payForLegalAidPort = 8080;
    public static final String serverName = "localhost";

//    dev

//    private static final String SCOPES = "https://graph.microsoft.com/.default";  // or other specific API scopes

    private static final Set<String> SCOPES = Set.of( "https://graph.microsoft.com/User.Read",   "https://graph.microsoft.com/User.ReadBasic.All");

    private static final String AUTHORITY = "https://login.microsoftonline.com/organizations/" + TENANT_ID + "/oauth2/v2.0/token";

    private static String WebApiUrlMe = "https://graph.microsoft.com/v1.0/me";
    private static String WebApiUrlMyManager = "https://graph.microsoft.com/v1.0/me/manager";


    /*
    {
    "@odata.context": "https://graph.microsoft.com/v1.0/$metadata#users/$entity",
    "@microsoft.graph.tips": "This request only returns a subset of the resource's properties. Your app will need to use $select to return non-default properties. To find out what other properties are available for this resource see https://learn.microsoft.com/graph/api/resources/user",
    "businessPhones": [],
    "displayName": "GPFD-testuser3",
    "givenName": "GPFD-testuser3",
    "jobTitle": null,
    "mail": null,
    "mobilePhone": null,
    "officeLocation": null,
    "preferredLanguage": null,
    "surname": null,
    "userPrincipalName": "GPFD-testuser3@devl.justice.gov.uk",
    "id": "b51f1e83-79bf-4fe3-b080-5873fe932142"
}
     */


    public static class AzureAuth {
        private static final String AUTHORITY = "https://login.microsoftonline.com/" + TENANT_ID + "/";

        public static String getAccessToken() throws Exception {
            ConfidentialClientApplication app = ConfidentialClientApplication.builder(
                            CLIENT_ID,
                            ClientCredentialFactory.createFromSecret(CLIENT_SECRET))
                    .authority(AUTHORITY)
                    .build();

            ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
                            Collections.singleton("https://graph.microsoft.com/.default"))
                    .build();

            CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
            IAuthenticationResult result = future.get();

            return result.accessToken();
        }
    }

    @Given("foo-bar")
    @SneakyThrows
    public void fooBar() {


//        try {
//            String accessToken = AzureAuth.getAccessToken();
//
////            GraphServiceClient.Builder<okhttp3.OkHttpClient, Request> builder = GraphServiceClient.builder();
////            builder.authenticationProvider(requestUrl -> {
////                requestUrl.getAuthority()
////            }); GraphServiceClient<Request> graphClient = builder
//////                    .authenticationProvider(request -> request.addHeader("Authorization", "Bearer " + accessToken))
////                    .buildClient();
//
//            GraphServiceClient<Request> graphClient = GraphServiceClient.builder()
//                    .authenticationProvider(request -> {
//                        Request.Builder builder = request.newBuilder();
//                        builder.header("Authorization", "Bearer " + accessToken);
//                        return builder.build();
//                    })
//                    .buildClient();
//
//            User user = graphClient.users().buildRequest().get().getCurrentPage().get(0);
//            System.out.println("User: " + user.displayName);
//            System.out.println("Email: " + user.mail);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // The auth provider will only authorize requests to
        // the allowed hosts, in this case Microsoft Graph
//        final String[] allowedHosts = new String[] { "graph.microsoft.com" };
//        final String[] graphScopes = new String[] { "User.Read" };
//
//        final DeviceCodeCredential credential = new DeviceCodeCredentialBuilder()
//                .clientId(clientId)
//                .challengeConsumer(challenge -> System.out.println(challenge.getMessage()))
//                .build();
//
//
//        final AzureIdentityAuthenticationProvider authProvider =
//                new AzureIdentityAuthenticationProvider(credential, allowedHosts, graphScopes);
//        GraphServiceClient graphServiceClient = GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();
//        final DefaultRequestAdapter adapter = new DefaultRequestAdapter(authProvider);
//
//        final GetUserApiClient client = new GetUserApiClient(adapter);
//        final User me = client.me().get();
//        System.out.println(me);
//        System.out.println(adapter);

//        IClientCredential credential = ClientCredentialFactory.createFromSecret(CLIENT_SECRET);
//        Random random = new Random(42);
//        ConfidentialClientApplication app = ConfidentialClientApplication
//                        .builder(CLIENT_ID, credential)
//                        .authority(AUTHORITY)
//                        .build();
//
//        ClientSecretCredentialBuilder clientSecretCredential = new ClientSecretCredentialBuilder();
//        clientSecretCredential.tenantId(TENANT_ID);
//        clientSecretCredential.clientId(CLIENT_ID);
//        clientSecretCredential.clientSecret(CLIENT_SECRET);
//        ClientSecretCredential build = clientSecretCredential.build();
//
//
////        final AzureIdentityAuthenticationProvider authProvider =
////                new AzureIdentityAuthenticationProvider(clientSecretCredential);
//
////        final DeviceCodeCredential credential = new DeviceCodeCredentialBuilder()
////                .clientId(clientId)
////                .challengeConsumer(challenge -> System.out.println(challenge.getMessage()))
////                .build();
//
//        final String clientId = "YOUR_CLIENT_ID";
//
//        // The auth provider will only authorize requests to
//        // the allowed hosts, in this case Microsoft Graph
//        final String[] allowedHosts = new String[] { "graph.microsoft.com" };
//        final String[] graphScopes = new String[] { "User.Read" };
//
//
//        final AzureIdentityAuthenticationProvider authProvider =
//                new AzureIdentityAuthenticationProvider(clientSecretCredential, allowedHosts, graphScopes);
//        final DefaultRequestAdapter adapter = new DefaultRequestAdapter(authProvider);
//
//        String password;
////        List<String> scopes;
//
////        UserNamePasswordParameters paramaters =
////                UserNamePasswordParameters.builder(
////                        SCOPES,
////                        "GPFD-testuser3@devl.justice.gov.uk",
////                        "Yalo621241?!".toCharArray()).build();
//
//        ClientCredentialParameters parameters =
//                ClientCredentialParameters
//                        .builder(Set.of("https://graph.microsoft.com/.default"))
//                        .build();
//
//        IAuthenticationResult join = app.acquireToken(parameters).join();
////        IAuthenticationResult result = app.acquireToken(paramaters).get();
////        UserNamePasswordParameters build = new UserNamePasswordParameters();
////        publicClientApplication.acquireToken(SilentParameters.builder()
////                        .account()
////                .build());
//        System.out.println(app);
//        System.out.println(join);
//        System.out.println(join.account());
//        System.out.println(join.metadata());
        System.out.println("");
    }

    @Given("the service is running")
    public void theServiceIsRunning() throws InterruptedException {
        if (System.getProperty("SERVICE").equals("local")) {
            assertTrue(isLocalServiceIsRunning());
        } else {
            Response actuatorResponse = checkServiceIsRunning();
            assertEquals(200, actuatorResponse.getStatusCode(), "Expected 200 OK response but received " + actuatorResponse.getStatusCode());
        }
    }

    @When("{string} cookie is provided for authentication")
    public void populateCookie(String cookieType) {
        cookie = new Cookie("JSESSIONID", "");
        //TODO This is not a permanent solution but allows us to update the cookie without changing the code for now!
        if (cookieType.equals("valid"))
            cookie.setValue(System.getProperty("cookie"));
    }

    @When("it calls the actuator endpoint")
    public void callActuatorApi() {
        response = makeGetCall("actuator", System.getProperty("BASE_URL"));
    }

    @Then("it should return a 200 response")
    public void itShouldReturn200() {
        assertEquals(200, response.getStatusCode(), "Expected 200 OK response but received " + response.getStatusCode());
    }

    @Then("it should return a 302 response")
    public void itShouldReturn302() {
        assertEquals(302, response.getStatusCode(), "Expected 302 OK response but received " + response.getStatusCode());
    }

    @Then("it should return a 404 response")
    public void itShouldReturn404() {
        assertEquals(404, response.getStatusCode(), "Expected 404 OK response but received " + response.getStatusCode());
    }

    @And("it calls the reports endpoint")
    public void callReportsEndpoint() {
        response = makeGetCall("reports", System.getProperty("BASE_URL"), cookie);
    }

    @Then("it should return a list of all the reports in the database")
    public void returnListOfReports() {
        List<Object> reportList = response.jsonPath().getList("reportList");
        assertFalse(reportList.isEmpty(), "Expected report details to be returned");
    }

    @Then("it should return error message {string}")
    public void returnErrorMessage(String errorMessage) throws Exception {
        JSONObject json = new JSONObject(response.getBody().asString());
        assertTrue(json.getString("error").contentEquals(errorMessage));
    }

    @And("it calls the get reports endpoint with id {string}")
    public void callReportEndpointForGivenId(String givenId) {
        response = makeGetCall("reports/" + givenId, System.getProperty("BASE_URL"), cookie);
    }

    @Then("it should return details for report with id {string}")
    public void returnReportDetails(String givenId) {
        assertEquals(givenId, response.jsonPath().getString("id"));
    }

    private boolean isLocalServiceIsRunning() throws InterruptedException {
        int timeout = 30; // Seconds
        while (timeout > 0) {
            try (Socket socket = new Socket(serverName, payForLegalAidPort)) {
                //Service is ready to be called
                return true;
            } catch (IOException e) {
                Thread.sleep(1000);
                timeout--;
            }
        }
        return false;
    }

    private Response checkServiceIsRunning() {
        return makeGetCall("actuator", System.getProperty("BASE_URL"));
    }

    private Response makeGetCall(String endpoint, String baseUrl) {
        return given().baseUri(baseUrl).redirects().follow(false)
                .get(endpoint);
    }

    private Response makeGetCall(String endpoint, String baseUrl, Cookie cookie) {
        return given().baseUri(baseUrl).redirects().follow(false).headers("cookie", cookie.getName() + "=" + cookie.getValue())
                .get(endpoint);
    }

}