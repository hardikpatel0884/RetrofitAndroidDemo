package com.test.retrofit.config;

import com.test.retrofit.response.ResponseImageUpload;
import com.test.retrofit.response.ResponseLogin;
import com.test.retrofit.response.ResponseRegister;
import com.test.retrofit.response.ResponseRemove;
import com.test.retrofit.response.ResponseUpdate;
import com.test.retrofit.response.ResponseUser;
import com.test.retrofit.response.task.ResponseGetTask;
import com.test.retrofit.response.task.ResponseTaskAdd;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

/**
 * Created by lcom151-one on 2/7/2018.
 */

public interface ApiInterface {

    @POST("user/login")
    @FormUrlEncoded
    Call<ResponseLogin> getLogin(@Field("username") String userName, @Field("password") String password);

    @GET("user/get")
    Call<ResponseUser> getUsers(@Header("apiKey") String apiKey);

    @POST("user/register")
    @FormUrlEncoded
    Call<ResponseRegister> registerUser(@Field("name") String name,@Field("email") String email,@Field("password") String password,@Field("image") String image);

    @POST("upload")
    @FormUrlEncoded
    Call<ResponseImageUpload> uploadImage(@Field("image") String image,@Header("apiKey") String apiKey);

    @DELETE("user/remove")
    Call<ResponseRemove> removeUser(@Header("apiKey") String apiKey);

    @PUT("user/update")
    @FormUrlEncoded
    Call<ResponseUpdate> updateUser(@Header("apiKey") String apiKey,@Field("name") String name);

    @POST("task/add")
    @FormUrlEncoded
    Call<ResponseTaskAdd> addTask(@Header("apiKey") String apiKey,@Field("title") String title,@Field("description") String description);

    @GET("task/get")
    Call<ResponseGetTask> getTask(@Header("apiKey") String apiKey);

    @Multipart
    @POST("upload/video")
//    Call<ResponseImageUpload> uploadVideo(@Header("apiKey") String apiKey, @Part("video") RequestBody file);
    Call<ResponseImageUpload> uploadVideo(@Header("apiKey") String apiKey, @Part MultipartBody.Part video);

    @FormUrlEncoded
    @POST("upload/video")
    Call<ResponseImageUpload> uploadVideo(@Header("apiKey") String apiKey, @Field("video") String video);
}
