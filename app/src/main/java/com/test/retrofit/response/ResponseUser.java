package com.test.retrofit.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.test.retrofit.model.ModelUser;

import java.util.List;

/**
 * Created by lcom151-one on 2/8/2018.
 */

public class ResponseUser {
    @SerializedName("users")
    @Expose
    private List<ModelUser> users = null;

    public List<ModelUser> getUsers() {
        return users;
    }

    public void setUsers(List<ModelUser> users) {
        this.users = users;
    }
}
