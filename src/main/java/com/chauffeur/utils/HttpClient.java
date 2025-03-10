package com.chauffeur.utils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
public class HttpClient {
    private final OkHttpClient client;
    private final Gson gson;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Inject
    public HttpClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public String postRequestString(String url, Object body) {
        String jsonBody = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(JSON, jsonBody);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.warn("Request failed: {} {}", response.code(), response.message());
                return null;
            }
            return response.body() != null ? response.body().string() : null;
        } catch (IOException e) {
            log.error("Failed to send request", e);
            return null;
        }
    }

    public <T> T postRequest(String url, Object body, Class<T> classOfT) {
        String jsonBody = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(JSON, jsonBody);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.warn("Request failed: {} {}", response.code(), response.message());
                return null;
            }
            String responseBody = response.body() != null ? response.body().string() : null;
            return responseBody != null ? gson.fromJson(responseBody, classOfT) : null;
        } catch (IOException e) {
            log.error("Failed to send request", e);
            return null;
        }
    }

    public String getRequestString(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.warn("Request failed: {} {}", response.code(), response.message());
                return null;
            }
            return response.body() != null ? response.body().string() : null;
        } catch (IOException e) {
            log.error("Failed to send request", e);
            return null;
        }
    }

    // Make a GET request to the specified URL and parse the json response
    public <T> T getRequest(String url, Class<T> classOfT) {
        String responseBody = getRequestString(url);
        if (responseBody != null) {
            try {
                return gson.fromJson(responseBody, classOfT);
            } catch (Exception e) {
                log.error("Failed to parse response", e);
                return null;
            }
        }
        return null;
    }
}