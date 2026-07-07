package com.helicalinsight.scheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.conn.ssl.*;
import java.util.ArrayList;




import java.net.URI;
import javax.net.ssl.*;
import com.google.gson.JsonObject;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class OkHttpSessionExample {
    private static final Logger logger = LoggerFactory.getLogger(OkHttpSessionExample.class);

    public static String makeCall(List<String> resourceUrl, JsonObject formData) throws Exception {
        TrustManager[] trustAllCertificates = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                          return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);


        // Create an HttpClient that trusts all certificates and sets connection timeout
        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .cookieHandler(new CookieManager())  // Handle session cookies
                .connectTimeout(java.time.Duration.ofSeconds(10))  // Set connection timeout
                .build();
logger.info("The url list is  "+resourceUrl);
        // Make login request
        String loginUrl = resourceUrl.get(0).replaceAll("//\\?", "/?");
        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(new URI(loginUrl))
                .GET()
                .build();

        HttpResponse<String> loginResponse = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());
        logger.info("Login url: " + loginUrl);
        logger.info("Login response: " + loginResponse.body());
        String respponse = "";
        // Check for successful login response before proceeding
        if (loginResponse.statusCode() == 200 || loginResponse.statusCode() == 302) {
            // Main URL call (after login)
            String mainUrl = resourceUrl.get(1).replace("//mock", "/mock");
            HttpRequest mainRequest = HttpRequest.newBuilder()
                    .uri(new URI(mainUrl))
                    .GET()
                    .build();

            HttpResponse<String> mainResponse = client.send(mainRequest, HttpResponse.BodyHandlers.ofString());
            logger.info("Main URL response: " + mainResponse.body());
            String encryptedFormData = new Base64().encodeToString(formData.toString().getBytes());
            String json =  // Replace with your actual JSON data

                    "type=hcr&serviceType=report&service=generateReport&formData=" + encryptedFormData;


            // Call 1 after main URL
            String url1 = resourceUrl.get(2)
                    .replace("//export", "/downloadReport")
                    .replace("//service", "/service")
                    .replace("//downloadReport", "/downloadReport");
            HttpRequest actualRequest = HttpRequest.newBuilder()
                    .uri(new URI(url1))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            HttpResponse<String> actualResponse = client.send(actualRequest, HttpResponse.BodyHandlers.ofString());
            logger.info("actualResponse response: " + actualResponse.body());
            respponse = actualResponse.body();

            // Call 2 after main URL
            String url2 = resourceUrl.get(3).replace("//logout","/logout");
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(new URI(url2))
                    .GET()
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            logger.info("Response from URL 2: " + response2.body());

        }
        return respponse;


    }

    public static String makeBinaryHttpCall(JsonObject formData, List<String> resourceUrl) throws Exception{

        // Disable SSL verification (unsafe, for testing only)
        TrustManager[] trustAllCertificates = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        // Create an HttpClient that trusts all certificates and sets connection timeout
        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .cookieHandler(new CookieManager())  // Handle session cookies
                .connectTimeout(java.time.Duration.ofSeconds(10))  // Set connection timeout
                .build();

        // Make login request
        String loginUrl = resourceUrl.get(0).replaceAll("//\\?", "/?");
        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(new URI(loginUrl))
                .GET()
                .build();

        HttpResponse<String> loginResponse = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());
        logger.info("The url list is  "+resourceUrl);
        logger.info("Login Url: " + loginUrl);
        logger.info("Login response: " + loginResponse.body());
        String respponse = "";
        // Check for successful login response before proceeding
        if (loginResponse.statusCode() == 200 || loginResponse.statusCode() == 302) {
            // Main URL call (after login)
            String mainUrl = resourceUrl.get(1).replace("//mock", "/mock");
            HttpRequest mainRequest = HttpRequest.newBuilder()
                    .uri(new URI(mainUrl))
                    .GET()
                    .build();

            HttpResponse<String> mainResponse = client.send(mainRequest, HttpResponse.BodyHandlers.ofString());
            logger.info("Main URL response: " + mainResponse.body());
            String json =  // Replace with your actual JSON data

                    "sendFileName=true&data=" + formData.toString();


            // Call 1 after main URL
            String url1 = resourceUrl.get(2)
                    .replace("//export", "/downloadReport")
                    .replace("//service", "/service")
                    .replace("//downloadReport", "/downloadReport");
            HttpRequest actualRequest = HttpRequest.newBuilder()
                    .uri(new URI(url1))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            HttpResponse<String> actualResponse = client.send(actualRequest, HttpResponse.BodyHandlers.ofString());
            logger.info("actualResponse response: " + actualResponse.body());
            respponse = actualResponse.body();

            // Call 2 after main URL
            String url2 = resourceUrl.get(3).replace("//logout","/logout");
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(new URI(url2))
                    .GET()
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            logger.info("Response from URL 2: " + response2.body());

        }
        return respponse;


    }
}