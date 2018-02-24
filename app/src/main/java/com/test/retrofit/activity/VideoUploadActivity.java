package com.test.retrofit.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.test.retrofit.R;
import com.test.retrofit.config.ApiClient;
import com.test.retrofit.config.ApiInterface;
import com.test.retrofit.response.ResponseImageUpload;
import com.test.retrofit.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoUploadActivity extends AppCompatActivity {

    private ApiInterface apiInterface;
    private VideoView vv;
    private String decodebleString;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        findViewById(R.id.pbar).setVisibility(View.GONE);
        vv=findViewById(R.id.videoView);
        vv.setVideoPath("http://192.168.200.51:3000/video/videofile.mp4");
        vv.start();
    }

    public void onSelect(View view) {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI),120);
    }

    public void onUpload(View view) {
        try{
            RequestBody videoBody=RequestBody.create(MediaType.parse("video/mp4"),file);
            MultipartBody.Part vFile=MultipartBody.Part.createFormData("video",file.getName(),videoBody);
            //Log.e("File name: ",vFile.body(). );
            Call<ResponseImageUpload> callUpload=apiInterface.uploadVideo(Utility.getSession().getApiKey(),vFile);
//            Log.e("onUpload: ",callUpload.request().body(). );
            findViewById(R.id.pbar).setVisibility(View.VISIBLE);
            callUpload.enqueue(new Callback<ResponseImageUpload>() {
                @Override
                public void onResponse(Call<ResponseImageUpload> call, Response<ResponseImageUpload> response) {
                    findViewById(R.id.pbar).setVisibility(View.GONE);
                    if (response.isSuccessful())
                    {
                        Toast.makeText(VideoUploadActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseImageUpload> call, Throwable t) {
                    findViewById(R.id.pbar).setVisibility(View.GONE);
                    Toast.makeText(VideoUploadActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    Log.e( "onFailure: ", t.toString());
                }
            });
        }catch (Exception e){e.printStackTrace();}




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode==120&& resultCode==RESULT_OK&&null!=data){
                Uri selectedVideo=data.getData();
                vv.setVideoURI(selectedVideo);
                vv.start();

                String[] filePathCollums={MediaStore.Video.Media.DATA};
                Cursor cursor=getContentResolver().query(selectedVideo,filePathCollums,null,null,null);
                cursor.moveToFirst();
                int columIndex=cursor.getColumnIndex(filePathCollums[0]);
                decodebleString=cursor.getString(columIndex);
                cursor.close();
                file=new File(decodebleString);
            }
        }catch (Exception e){e.printStackTrace();}
    }
}
