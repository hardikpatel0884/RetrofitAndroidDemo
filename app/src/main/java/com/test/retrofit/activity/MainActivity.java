package com.test.retrofit.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.test.retrofit.R;
import com.test.retrofit.adapter.UserAdapter;
import com.test.retrofit.config.ApiClient;
import com.test.retrofit.config.ApiInterface;
import com.test.retrofit.model.ModelUser;
import com.test.retrofit.response.ResponseRegister;
import com.test.retrofit.response.ResponseRemove;
import com.test.retrofit.response.ResponseUpdate;
import com.test.retrofit.response.ResponseUser;
import com.test.retrofit.utils.CircleTransform;
import com.test.retrofit.utils.CommonUtils;
import com.test.retrofit.utils.ImageFilePath;
import com.test.retrofit.utils.RecyclerItemTouchHelper;
import com.test.retrofit.utils.UserSession;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements UserAdapter.UserClickInterface,RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private List<ModelUser> users = new ArrayList<>();
    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private TextView tvName, tvEmail, tvRegister, tvTitle;
    private TextInputEditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirmPassword;
    private SwipeRefreshLayout srlUser;
    private LinearLayout llRegister, llDetail;
    private ImageView ivClose, ivProfile, ivEdit, ivDelete, ivChangePassword;
    private BottomSheetDialog bottomSheetDialog;
    private ProgressBar progressBar;
    private ApiInterface apiService;
    private String strImage, selectedImagePath, apiKey;
    private UserSession session;
    private int userPosition;
    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvUsers = findViewById(R.id.rv_users);
        adapter = new UserAdapter(users, this);
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        rvUsers.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));

        View view = getLayoutInflater().inflate(R.layout.user_detail, null, false);
        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
        ivClose = view.findViewById(R.id.iv_close);

        tvRegister = view.findViewById(R.id.tv_register);
        tvTitle = view.findViewById(R.id.tv_title);
        edtName = view.findViewById(R.id.edt_user_name);
        edtEmail = view.findViewById(R.id.edt_user_email);
        ivProfile = view.findViewById(R.id.iv_profile);
        edtConfirmPassword = view.findViewById(R.id.edt_user_confirm_password);
        edtPassword = view.findViewById(R.id.edt_user_password);
        llRegister = view.findViewById(R.id.ll_register);
        llDetail = view.findViewById(R.id.ll_details);
        progressBar = view.findViewById(R.id.progress);
        srlUser = findViewById(R.id.srl_user);
        ivEdit = view.findViewById(R.id.iv_edit);
        ivDelete = view.findViewById(R.id.iv_delete);
        ivChangePassword = view.findViewById(R.id.iv_change_password);
        tilName = view.findViewById(R.id.til_user_name);
        tilEmail = view.findViewById(R.id.til_user_email);
        tilPassword = view.findViewById(R.id.til_user_password);
        tilConfirmPassword = view.findViewById(R.id.til_user_confirm_password);

        ivEdit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Edit details", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        ivDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Delete user details", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        ivChangePassword.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Change Password", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        session = new UserSession(MainActivity.this);

        Log.e(TAG, "onCreate: api: " + session.getApiKey());
        Log.e(TAG, "onCreate: name: " + session.getUserDetails().get(session.KEY_NAME));

        if (!session.isLogin()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        srlUser.post(new Runnable() {
            @Override
            public void run() {
                getUserList();
            }
        });

        srlUser.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserList();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.setCancelable(false);
                bottomSheetDialog.dismiss();
            }
        });

        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidName();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidEmail();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidPassword();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setContentView(view);

        try{
            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvUsers);
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_image:
                startActivity(new Intent(MainActivity.this, ImageUploadActivity.class));
                break;
            case R.id.action_video:
                startActivity(new Intent(MainActivity.this,VideoUploadActivity.class));
                break;
            case R.id.action_task:
                startActivity(new Intent(MainActivity.this, TaskActivity.class));
                break;
            case R.id.action_logout:
                session.logout();
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClickUser(final int position) {
        tvTitle.setText("User Details");
        llRegister.setVisibility(View.GONE);
        llDetail.setVisibility(View.VISIBLE);

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPermission()) {
                    Intent intent = new Intent(MainActivity.this, ImageUploadActivity.class);
                    intent.putExtra("apiKey", users.get(position).getApiKey());
                    startActivityForResult(intent, 101);
                }
            }
        });
        apiKey = users.get(position).getApiKey();
        userPosition = position;
        String url = getResources().getString(R.string.host) + "user/" + users.get(position).getImage();
        Picasso.with(MainActivity.this).load(url).skipMemoryCache().transform(new CircleTransform()).into(ivProfile);
        tvName.setText("Name: " + users.get(position).getName());
        tvEmail.setText("Email: " + users.get(position).getEmail());
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            getUserList();
            bottomSheetDialog.dismiss();
        }
        // Image Selctor
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
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 8;
                            Bitmap bitmap = BitmapFactory.decodeFile(newImg, options);
                            ivProfile.setImageBitmap(bitmap);
                            Toast.makeText(this, "path : " + newImg, Toast.LENGTH_SHORT).show();
                            compressImage(newImg);
                        } else {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 8;
                            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
                            ivProfile.setImageBitmap(bitmap);
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
        strImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
    }

    public void onRegister(View view) {
        try {
            strImage = "";
            tvTitle.setText("Registration");
            llRegister.setVisibility(View.VISIBLE);
            llDetail.setVisibility(View.GONE);
            ivProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_account));
            bottomSheetDialog.show();
            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getPermission()) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 12);
                    }
                }
            });
            tvRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isValidName() && isValidEmail() && isValidPassword() && isConfirmPassword() && !isEmailExist())
                        startRegister();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRegister() {
        final String name = edtName.getText().toString().trim();
        final String email = edtEmail.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        Call<ResponseRegister> call = apiService.registerUser(name, email, password, strImage);
        call.enqueue(new Callback<ResponseRegister>() {
            @Override
            public void onResponse(Call<ResponseRegister> call, Response<ResponseRegister> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body().toString());
                    if (response.body().getRegister().getError()) {
                        Toast.makeText(MainActivity.this, "Register Fail: " + response.body().getRegister().getMessage(), Toast.LENGTH_SHORT).show();

                    } else {
                        ModelUser user = new ModelUser();
                        user.setName(name);
                        user.setEmail(email);
                        user.setImage(response.body().getRegister().getUser().getImage());
                        user.setApiKey(response.headers().get("apiKey"));
                        user.setId(response.body().getRegister().getUser().getId());
                        users.add(user);
                        adapter.notifyDataSetChanged();
                        rvUsers.scrollToPosition(users.size());
                        Toast.makeText(MainActivity.this, "Register Success : " + response.headers().get("apiKey"), Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        edtEmail.setText(null);
                        edtConfirmPassword.setText(null);
                        edtName.setText(null);
                        edtPassword.setText(null);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Register Somthing wrong " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseRegister> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Register Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidName() {
        if (edtName.getText().toString().trim().split(" ").length == 3) {
            tilName.setErrorEnabled(false);
            return true;
        }
        tilName.setErrorEnabled(true);
        tilName.setError("Enter valid name {FN MN LN}");
        edtName.requestFocus();
        return false;
    }

    private boolean isValidEmail() {
        if (Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString().trim()).matches()) {
            tilEmail.setErrorEnabled(false);
            return true;
        }
        tilEmail.setErrorEnabled(true);
        tilEmail.setError("Please enter valid email");
        edtEmail.requestFocus();
        return false;
    }

    private boolean isValidPassword() {
        if (edtPassword.getText().toString().trim().length() > 5) {
            tilPassword.setErrorEnabled(false);
            return true;
        }
        tilPassword.setErrorEnabled(true);
        tilPassword.setError("Password is more then 5 character");
        edtPassword.requestFocus();
        return false;
    }

    private boolean isConfirmPassword() {
        if (edtPassword.getText().toString().trim().equals(edtConfirmPassword.getText().toString().trim())) {
            tilConfirmPassword.setErrorEnabled(false);
            return true;
        }
        tilConfirmPassword.setErrorEnabled(true);
        tilConfirmPassword.setError("Password does not match");
        edtConfirmPassword.requestFocus();
        return false;
    }

    private boolean isEmailExist() {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(edtEmail.getText().toString().trim())) {
                tilEmail.setErrorEnabled(true);
                tilEmail.setError("this email already exist");
                edtEmail.requestFocus();
                return true;
            }
        }
        tilEmail.setErrorEnabled(false);
        return false;
    }

    private void getUserList() {
        srlUser.setRefreshing(true);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseUser> call = apiService.getUsers(session.getApiKey());
        call.enqueue(new Callback<ResponseUser>() {
            @Override
            public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                srlUser.setRefreshing(false);
                findViewById(R.id.progress).setVisibility(View.GONE);
                int code = response.code();
                if (code == 200) {
                    Log.e(TAG, "onResponse: response " + response.body());
                    users.clear();
                    users.addAll(response.body().getUsers());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Oopsa  " + code, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseUser> call, Throwable t) {
                srlUser.setRefreshing(false);
                findViewById(R.id.progress).setVisibility(View.GONE);
                Log.e(TAG, "onFailure: " + t);
                Toast.makeText(MainActivity.this, "Oopsa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean getPermission() {

        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
                    return false;
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertBuilder.setCancelable(false);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Storage permission is necessary to upload image!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent itShowSetting = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            itShowSetting.setData(uri);
                            startActivityForResult(itShowSetting, MY_PERMISSIONS_REQUEST_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                    return false;
                }
            } else {
                return true;
            }
        }
        return true;
    }

    public void onDeleteUser(View view) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete")
                .setMessage("are you sure you want to delete this user")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Call<ResponseRemove> callRemove = apiService.removeUser(apiKey);
                        callRemove.enqueue(new Callback<ResponseRemove>() {
                            @Override
                            public void onResponse(Call<ResponseRemove> call, Response<ResponseRemove> response) {
                                if (response.isSuccessful()) {
                                    if (response.body().getError()) {
                                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        users.remove(userPosition);
                                        adapter.notifyDataSetChanged();
//                                        getUserList();
                                        Toast.makeText(MainActivity.this, "User Removed", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseRemove> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    public void onUpdateUser(View view) {
        onShowUpdate(false, users.get(userPosition).getName());
    }

    private void onShowUpdate(boolean error, String name) {
        try {
            final AlertDialog.Builder updateDialoge = new AlertDialog.Builder(MainActivity.this);
            LinearLayout fl = new LinearLayout(MainActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            fl.setLayoutParams(params);
            fl.setOrientation(LinearLayout.VERTICAL);
            fl.setPadding(10, 10, 10, 10);

            final TextInputLayout tilUpdate = new TextInputLayout(MainActivity.this);
            if (error) {
                tilUpdate.setErrorEnabled(true);
                tilUpdate.setError("Enter Valid Name {FN MN LN}");
            }
            final TextInputEditText edtUpdate = new TextInputEditText(MainActivity.this);
            edtUpdate.setHint("Enter Name");
            edtUpdate.setText(name);
            edtUpdate.setSingleLine(true);
            edtUpdate.requestFocus();
            tilUpdate.addView(edtUpdate);
            fl.addView(tilUpdate);
            updateDialoge.setView(fl).setCancelable(false).setTitle("Update Name").setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (edtUpdate.getText().toString().trim().split(" ").length == 3) {
                        onStartUpdate(edtUpdate.getText().toString().trim());
                    } else {
                        onShowUpdate(true, edtUpdate.getText().toString().trim());
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "Update Cancle", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onStartUpdate(final String name) {
        if (name != null) {
            Call<ResponseUpdate> callUpdate = apiService.updateUser(apiKey, name);
            callUpdate.enqueue(new Callback<ResponseUpdate>() {
                @Override
                public void onResponse(Call<ResponseUpdate> call, Response<ResponseUpdate> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError()) {
                            Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            users.get(userPosition).setName(name);
                            adapter.notifyItemChanged(userPosition);
                            bottomSheetDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseUpdate> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "name not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof UserAdapter.UserHolder){
            if(direction==ItemTouchHelper.LEFT){
                Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
                users.remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(this, "View", Toast.LENGTH_SHORT).show();
                this.onClickUser(viewHolder.getAdapterPosition());
            }
        }
    }
}

