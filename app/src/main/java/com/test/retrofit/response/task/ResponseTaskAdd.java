package com.test.retrofit.response.task;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.test.retrofit.model.ModelTask;

/**
 * Created by lcom151-one on 2/16/2018.
 */

public class ResponseTaskAdd {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("task")
    @Expose
    private ModelTask task;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ModelTask getTask() {
        return task;
    }

    public void setTask(ModelTask task) {
        this.task = task;
    }
}
