package com.test.retrofit.utils;

import android.app.Application;

import com.test.retrofit.config.ApiClient;
import com.test.retrofit.config.ApiInterface;

/**
 * Created by lcom151-one on 2/16/2018.
 */

public class Utility extends Application {

    private static ApiInterface apiService;
    private static UserSession session;


    @Override
    public void onCreate() {
        super.onCreate();
        session=new UserSession(Utility.this);
        apiService= ApiClient.getClient().create(ApiInterface.class);
    }

    public static ApiInterface getApiService() {
        return apiService;
    }

    public static UserSession getSession() {
        return session;
    }
}
