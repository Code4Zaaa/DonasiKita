package com.vriza.donasikita.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://donasikita.forzaa.lol";
    private static final String IMAGE_BASE_URL = BASE_URL + "/storage/";
    private static final String API_BASE_URL = BASE_URL + "/api/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }

    public static String getImageUrl() {
        return IMAGE_BASE_URL;
    }
}