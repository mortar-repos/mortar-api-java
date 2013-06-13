/*
 * Copyright 2013 Mortar Data Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

/**
 * Provides the client for accessing the Mortar v2 API.
 * 
 * @see <a href="http://help.mortardata.com/reference/api/api_version_2" target="_blank">
 * http://help.mortardata.com/reference/api/api_version_2</a>
 */
public class API {

    private static final String DEFAULT_SCHEME = "https";
    private static final String DEFAULT_HOST = "api.mortardata.com";
    
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    
    private String email;
    private String apiKey;
    private HttpRequestFactory requestFactory;
    private String scheme;
    private String host;
    
    /**
     * Constructs a new API client to invoke methods on the Mortar V2 API.
     * 
     * @param email Email associated with your Mortar user
     * @param apiKey API key for your Mortar user
     */
    public API(String email, String apiKey) {
        this(email, apiKey, API.DEFAULT_SCHEME, API.DEFAULT_HOST);
    }
    
    /**
     * Constructs a new API client for custom API host and scheme.
     * 
     * @param email Email associated with your Mortar user
     * @param apiKey Email associated with your Mortar user
     * @param scheme http or https
     * @param host API host (e.g. api.mortardata.com) 
     */
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
    HttpRequest buildHttpGetRequest(String path) throws IOException {
        return requestFactory.buildGetRequest(new GenericUrl(getURLString(path)));
    }

    HttpRequest buildHttpPostRequest(String path, Object data) throws IOException {
        HttpContent postContent = new JsonHttpContent(JSON_FACTORY, data); 
        return requestFactory.buildPostRequest(new GenericUrl(getURLString(path)), postContent);
    }

    HttpRequest buildHttpPutRequest(String path, Object data) throws IOException {
        HttpContent postContent = new JsonHttpContent(JSON_FACTORY, data); 
        return requestFactory.buildPutRequest(new GenericUrl(getURLString(path)), postContent);
    }

    HttpRequest buildHttpDeleteRequest(String path) throws IOException {
        return requestFactory.buildDeleteRequest(new GenericUrl(getURLString(path)));
    }
    
    private String getURLString(String path) {
        return this.scheme + "://" + this.host + "/v2/" + path;
    }

    @Override
    public String toString() {
        return "API [email=" + email + "]";
    }

}
