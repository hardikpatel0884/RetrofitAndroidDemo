package com.test.retrofit.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.test.retrofit.model.ModelLogin;

/**
 * Created by lcom151-one on 2/15/2018.
 */

public class ResponseLogin {
    @SerializedName("Login")
    @Expose
    private ModelLogin login;

    public ModelLogin getLogin() {
        return login;
    }

    public void setLogin(ModelLogin login) {
        this.login = login;
    }
}
