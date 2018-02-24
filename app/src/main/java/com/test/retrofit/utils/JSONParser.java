package com.test.retrofit.utils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Hardik Patel on 19-08-2017.
 */

public class JSONParser {


    static JSONObject jarray = null;


    public JSONParser() {

    }

    public JSONObject getJSONFromUrl(String url) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statecode = statusLine.getStatusCode();
            if (statecode == 200) {
                HttpEntity entity = httpResponse.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reder = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reder.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } else {
                Log.d("Connection not found", "Chack Connection");
            }
        } catch (Exception ex) {
            Log.d("Error", ex.toString());
        } finally {
            try {
                httpGet.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

        }

        try {
            Log.e("getJSONFromUrl: ", stringBuilder.toString());
            jarray = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return jarray;
    }
}
