package uk.gov.laa.pfla;

import com.microsoft.graph.logger.ILogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AzureGraph {



    public static void main(String[] args) {
        try {
            String accessToken = StepDefinitions.AzureAuth.getAccessToken();
            ILogger logger = new DefaultLogger();
            logger.setLoggingLevel(LoggerLevel.DEBUG);
            System.out.println("Access Token: " + accessToken);

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request originalRequest = chain.request();
                        Request newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer " + accessToken)
                                .build();
                        return chain.proceed(newRequest);
                    })
                    .build();

            Request request = new Request.Builder()
                    .url( "https://dev.get-legal-aid-data.service.justice.gov.uk/reports?continue")
                    .build();

            Response execute = httpClient.newCall(request).execute();

            ResponseBody body = execute.body();
            System.out.println(body.string());
            System.out.println(body.toString());
        } catch (Exception e) {
            System.err.println("Error occurred: ");
            e.printStackTrace();
        }
    }
}
