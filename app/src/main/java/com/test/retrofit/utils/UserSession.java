package com.test.retrofit.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.test.retrofit.activity.LoginActivity;
import com.test.retrofit.activity.MainActivity;
import com.test.retrofit.model.ModelUser;

import java.util.HashMap;

/**
 * Created by lcom151-one on 2/15/2018.
 */

public class UserSession {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private static final int PREF_MODE=0;
    private static final String PREF_NAME="loginUser";
    private Context context;

    private static final String IS_LOGIN="isLogin";

    public static final String KEY_NAME="name";
    public static final String KEY_EMAIL="email";
    public static final String KEY_IMAGE="image";
    public static final String API_KEY="apiKey";

    public UserSession(Context context) {
        this.context = context;
        this.sp=context.getSharedPreferences(this.PREF_NAME,this.PREF_MODE);
        this.editor=sp.edit();
    }

    public void createUser(ModelUser user,String apiKey){
        this.editor.putBoolean(this.IS_LOGIN,true);
        this.editor.putString(this.KEY_NAME,user.getName());
        this.editor.putString(this.KEY_EMAIL,user.getEmail());
        this.editor.putString(this.KEY_IMAGE,user.getImage());
        this.editor.putString(this.API_KEY,apiKey);
        this.editor.commit();
        this.context.startActivity(new Intent(this.context, MainActivity.class));
    }

    public String getApiKey(){
        return sp.getString(this.API_KEY,null);
    }

    public boolean isLogin(){
        return sp.getBoolean(this.IS_LOGIN,false);
    }

    public HashMap<String,String> getUserDetails(){
        HashMap<String,String> user= new HashMap<String, String>();
        user.put(this.KEY_NAME,sp.getString(this.KEY_NAME,null));
        user.put(this.KEY_EMAIL,sp.getString(this.KEY_EMAIL,null));
        user.put(this.KEY_IMAGE,sp.getString(this.KEY_IMAGE,null));
        return user;
    }

    public void logout(){
        try{
            //clear all data form shared preference
            this.editor.remove(this.KEY_NAME);
            this.editor.remove(this.KEY_EMAIL);
            this.editor.remove(this.KEY_IMAGE);
            this.editor.clear();
            this.editor.commit();
            this.context.startActivity(new Intent(this.context,LoginActivity.class));
        }catch (Exception e){e.printStackTrace();}
    }
}
