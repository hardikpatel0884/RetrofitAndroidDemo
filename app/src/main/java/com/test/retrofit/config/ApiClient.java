package com.test.retrofit.config;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lcom151-one on 2/7/2018.
 */

public class ApiClient {
    public static final String BASE_URL="http://192.168.200.51:3000/";
    private static Retrofit retrofit=null;

    public static Retrofit getClient(){
        if (retrofit==null) {
            retrofit=new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
