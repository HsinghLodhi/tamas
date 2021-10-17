package com.video.tamas.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;
import com.video.tamas.Utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText etEmailAddressForgot;
    private Button btnGetCode;
    private MaterialDialog materialDialog;
    private NetworkUtils networkUtils;
    private DeviceResourceManager resourceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        etEmailAddressForgot = findViewById(R.id.etEmailAddressForgot);
        btnGetCode = findViewById(R.id.btnGetCode);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Forget Password");
        actionBar.setDisplayHomeAsUpEnabled(true);
        resourceManager = new DeviceResourceManager(ForgotPasswordActivity.this);
        networkUtils = new NetworkUtils(this);
        resourceManager.clearSharedPref(Config.USER_NAME);
        resourceManager.clearSharedPref(Config.USER_ID);
        resourceManager.clearSharedPref(Config.EMAIL_ADDRESS);
        btnGetCode.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(etEmailAddressForgot.getText().toString())) {
                if (StringUtils.isValidEmail(etEmailAddressForgot.getText().toString())) {
                    doForgetPassword(etEmailAddressForgot.getText().toString().trim());
                } else {
                    etEmailAddressForgot.setError("Please enter valid email address");
                    etEmailAddressForgot.requestFocus();
                }
            } else {
                etEmailAddressForgot.setError("Please enter email address");
                etEmailAddressForgot.requestFocus();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            resourceManager.clearSharedPref("USER_ID");
            resourceManager.clearSharedPref(Config.EMAIL_ADDRESS);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private void doForgetPassword(String emailAddress) {
        materialDialog = new MaterialDialog.Builder(this)
                .content("Please Wait...")
                .cancelable(false)
                .progress(true, 0)
                .show();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", emailAddress);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.ForgotPassword, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                Log.w("result", result);
                materialDialog.dismiss();
                try {
                    Log.v("dip", "Login Response : " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    System.out.println("jsonobj:" + jsonObject.toString());
                    String responce = jsonObject.optString("responce");
                    if (responce.equalsIgnoreCase("true")) {
                        String userId = jsonObject.optString("id");
                        resourceManager.addToSharedPref("USER_ID", userId);
                        resourceManager.addToSharedPref(Config.EMAIL_ADDRESS, emailAddress);
                        startActivity(new Intent(ForgotPasswordActivity.this, VerifyOtpActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "email not found", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {

                materialDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, result, Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
        resourceManager.clearSharedPref("USER_ID");
        resourceManager.clearSharedPref(Config.EMAIL_ADDRESS);
        finish();
    }
}
