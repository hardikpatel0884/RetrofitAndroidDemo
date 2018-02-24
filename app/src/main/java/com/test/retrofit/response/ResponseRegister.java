package com.test.retrofit.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.test.retrofit.model.ModelRegister;

/**
 * Created by lcom151-one on 2/8/2018.
 */

public class ResponseRegister {
    @SerializedName("Register")
    @Expose
    private ModelRegister register;

    public ModelRegister getRegister() {
        return register;
    }

    public void setRegister(ModelRegister register) {
        this.register = register;
    }
}
