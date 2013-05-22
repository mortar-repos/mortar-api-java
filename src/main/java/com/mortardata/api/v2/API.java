package com.mortardata.api.v2;


import java.io.IOException;

import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

public class API {

    private static final String SCHEME = "https";
    private static final String HOST = "app.mortardata.com";
    
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    
    private String email;
    private String apiKey;
    private HttpRequestFactory requestFactory;
    private String scheme;
    private String host;
    
    public API(String email, String apiKey) {
        this(email, apiKey, API.SCHEME, API.HOST);
    }
    
    public API(String email, String apiKey, String scheme, String host) {
        this.email = email;
        this.apiKey = apiKey;
        this.scheme = scheme;
        this.host = host;
        this.requestFactory = createHttpRequestFactory();
    }
    
    private HttpRequestFactory createHttpRequestFactory() {
        final BasicAuthentication basicAuth = new BasicAuthentication(this.email, this.apiKey);
        return HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
                basicAuth.initialize(request);
                // do retries with exponential backoff when requests fail
                request.setUnsuccessfulResponseHandler(
                        new HttpBackOffUnsuccessfulResponseHandler(new ExponentialBackOff()));
                request.setParser(new JsonObjectParser(JSON_FACTORY));
            }
          });
    }
    public HttpRequest buildHttpGetRequest(String path) throws IOException {
        return requestFactory.buildGetRequest(new GenericUrl(getURLString(path)));
    }

    public HttpRequest buildHttpPostRequest(String path, Object data) throws IOException {
        HttpContent postContent = new JsonHttpContent(JSON_FACTORY, data); 
        return requestFactory.buildPostRequest(new GenericUrl(getURLString(path)), postContent);
    }

    public HttpRequest buildHttpPutRequest(String path, Object data) throws IOException {
        HttpContent postContent = new JsonHttpContent(JSON_FACTORY, data); 
        return requestFactory.buildPutRequest(new GenericUrl(getURLString(path)), postContent);
    }

    public HttpRequest buildHttpDeleteRequest(String path) throws IOException {
        return requestFactory.buildDeleteRequest(new GenericUrl(getURLString(path)));
    }
    
    private String getURLString(String path) {
        return this.scheme + "://" + this.host + "/v2/" + path;
    }
    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        API api = new API("XXXXX", "XXXXXX");
        Jobs.JobsList jobsList = new Jobs(api).getJobs();
        System.out.println("Output:\n" + jobsList.jobCount);
        for (Jobs.Job job : jobsList.jobs) {
            System.out.println("Job: " + job.scriptName + "," +  job.statusCode + "," + job.statusDescription +  "," + job.scriptParameters + "," + job.gitRef + "," + job.startTimestamp); 
        }
    }

}
