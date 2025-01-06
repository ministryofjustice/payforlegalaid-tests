package uk.gov.laa.pfla.utils;

import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.UsernamePasswordCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;

public class AzureAdAuthUtil {

    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String CLIENT_SECRET =  "CLIENT_SECRET";
    private static final String TENANT_ID = "TENANT_ID";
    private static final String SCOPES = "https://graph.microsoft.com/User.Read";  // or other specific API scopes
    private static final String AUTHORITY = "https://login.microsoftonline.com/" + TENANT_ID + "/oauth2/v2.0/token";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";

    public static String authenticateAndGetToken() {
        ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .tenantId(TENANT_ID)
                .build();

        return credential.getToken(new TokenRequestContext().addScopes(SCOPES)).block().getToken();
    }

    public static String authenticateAndGetTokenWithUserDetails() {
        UsernamePasswordCredential credential = new UsernamePasswordCredentialBuilder()
                .clientId(CLIENT_ID)
                .tenantId(TENANT_ID)
                .username(USERNAME)
                .password(PASSWORD)
                .build();

        return credential.getToken(new TokenRequestContext().addScopes(SCOPES)).block().getToken();
    }
}
