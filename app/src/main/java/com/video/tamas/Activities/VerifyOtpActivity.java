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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyOtpActivity extends AppCompatActivity {

    private Button btnSubmitOtp;
    private TextView tvEmailInOtp;
    private EditText etOtpField;
    private MaterialDialog materialDialog;
    private NetworkUtils networkUtils;
    private DeviceResourceManager resourceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        btnSubmitOtp = findViewById(R.id.btnSubmitOtp);
        tvEmailInOtp = findViewById(R.id.tvEmailAddressInVerifyOtp);
        etOtpField = findViewById(R.id.etOtpField);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Verify OTP");
        actionBar.setDisplayHomeAsUpEnabled(true);
        resourceManager = new DeviceResourceManager(VerifyOtpActivity.this);
        networkUtils = new NetworkUtils(this);
        tvEmailInOtp.setText(resourceManager.getDataFromSharedPref(Config.EMAIL_ADDRESS));
        btnSubmitOtp.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(etOtpField.getText().toString())) {
                if (etOtpField.getText().toString().length() == 5) {
                    doVerifyOtp(etOtpField.getText().toString().trim());
                } else {
                    etOtpField.setError("Please enter five pin otp");
                    etOtpField.requestFocus();
                }
            } else {
                etOtpField.setError("Please enter otp pin");
                etOtpField.requestFocus();

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            startActivity(new Intent(VerifyOtpActivity.this, ForgotPasswordActivity.class));
            resourceManager.clearSharedPref("USER_ID");
            resourceManager.clearSharedPref(Config.EMAIL_ADDRESS);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private void doVerifyOtp(String otp) {
        materialDialog = new MaterialDialog.Builder(this)
                .content("Please Wait...")
                .cancelable(false)
                .progress(true, 0)
                .show();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", resourceManager.getDataFromSharedPref("USER_ID"));
            jsonBody.put("otp", otp);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.CheckOtp, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                Log.w("result", result);
                materialDialog.dismiss();
                try {
                    Log.v("dip", "verify Response : " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String responce = jsonObject.optString("responce");
                    if (responce.equalsIgnoreCase("true")) {
                        String userId = jsonObject.optString("id");
                        resourceManager.addToSharedPref("USER_ID", userId);
                        startActivity(new Intent(VerifyOtpActivity.this, UpdatePasswordActivity.class));
                        finish();
                    } else {
                        Toast.makeText(VerifyOtpActivity.this, "wrong otp enter", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {

                materialDialog.dismiss();
                Toast.makeText(VerifyOtpActivity.this, result, Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(VerifyOtpActivity.this, ForgotPasswordActivity.class));
        resourceManager.clearSharedPref("USER_ID");
        resourceManager.clearSharedPref(Config.EMAIL_ADDRESS);
        finish();
    }
}
