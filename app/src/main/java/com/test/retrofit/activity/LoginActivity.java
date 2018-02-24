package com.test.retrofit.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.test.retrofit.R;
import com.test.retrofit.config.ApiClient;
import com.test.retrofit.config.ApiInterface;
import com.test.retrofit.response.ResponseLogin;
import com.test.retrofit.response.ResponseRegister;
import com.test.retrofit.utils.UserSession;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilPassword, tilUserName;
    private TextInputEditText edtUserName, edtPassword;
    private CheckBox cbShowPassword;
    private ApiInterface apiService;
    private UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        tilUserName = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        edtUserName = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        cbShowPassword = findViewById(R.id.cb_showpassword);
        edtUserName.addTextChangedListener(new TextWatcher() {
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

        try {
            session = new UserSession(LoginActivity.this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
            apiService = ApiClient.getClient().create(ApiInterface.class);
            if (session.isLogin()) {
                findViewById(R.id.ll_login).setVisibility(View.GONE);
                findViewById(R.id.iv_launch).setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isValidEmail() {
        if (Patterns.EMAIL_ADDRESS.matcher(edtUserName.getText().toString().trim()).matches()) {
            tilUserName.setErrorEnabled(false);
            return true;
        }
        tilUserName.setErrorEnabled(true);
        tilUserName.setError("Please enter valid email");
        tilUserName.requestFocus();
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

    public void onLogin(View view) {
        if (isValidEmail() && isValidPassword()) {
            this.onStartLogin();
        }
    }

    public void onShowPassword(View view) {
        if (cbShowPassword.isChecked()) {
            edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    private void onStartLogin() {
        Call<ResponseLogin> callLogin = apiService.getLogin(edtUserName.getText().toString().trim(), edtPassword.getText().toString().trim());

        callLogin.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                if (response.isSuccessful()) {
                    if (!response.body().getLogin().getError()) {
                        session.createUser(response.body().getLogin().getUser(), response.headers().get("apiKey"));
                        finish();
                    } else {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginActivity.this);
                        alertBuilder.setCancelable(false);
                        alertBuilder.setTitle("Login Fail");
                        alertBuilder.setMessage("Login Fail, please enter valid data to continue");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error: " + response.body().getLogin().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "fail", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
