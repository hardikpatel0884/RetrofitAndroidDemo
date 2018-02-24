package com.test.retrofit.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.test.retrofit.R;
import com.test.retrofit.config.ApiClient;
import com.test.retrofit.config.ApiInterface;
import com.test.retrofit.response.ResponseImageUpload;
import com.test.retrofit.utils.CommonUtils;
import com.test.retrofit.utils.ImageFilePath;

import java.io.ByteArrayOutputStream;
import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUploadActivity extends AppCompatActivity {
    String selectedImagePath, compressImage;
    private static final String TAG = ImageUploadActivity.class.getSimpleName();
    private ApiInterface apiInterface;
    private String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Intent getIntent = getIntent();
        apiKey = getIntent.getStringExtra("apiKey");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 12);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 12) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);
                try {
                    if (selectedImagePath != null) {
                        File image = new File(selectedImagePath);
                        Log.e(TAG, "onActivityResult: image size " + image.length() / 1024);
                        if ((image.length() / 1024) > 500) {
                            String newImg = CommonUtils.compressImage(selectedImagePath);
                            Toast.makeText(this, "path : " + newImg, Toast.LENGTH_SHORT).show();
                            compressImage(newImg);
                        } else {
                            compressImage(selectedImagePath);
                        }
                    } else {
                        Toast.makeText(this, "unable to find image", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void compressImage(String imagePath) {
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        compressImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        onUploadImage(compressImage);
    }

    private void onUploadImage(String imageEncode) {
        try {
            final Call<ResponseImageUpload> imageUploadCall = apiInterface.uploadImage(imageEncode, apiKey);
            imageUploadCall.enqueue(new Callback<ResponseImageUpload>() {
                @Override
                public void onResponse(Call<ResponseImageUpload> call, Response<ResponseImageUpload> response) {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (!response.body().getError()) {
                        Log.e(TAG, "onResponse: success");
                        Toast.makeText(ImageUploadActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                        finishActivity(101);
                        finish();
                    } else {
                        Toast.makeText(ImageUploadActivity.this, "Image upload fail", Toast.LENGTH_SHORT).show();

                    }
                    imageUploadCall.cancel();
                }

                @Override
                public void onFailure(Call<ResponseImageUpload> call, Throwable t) {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    Toast.makeText(ImageUploadActivity.this, "Image upload fail", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
