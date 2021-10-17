package com.video.tamas.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdatePasswordActivity extends AppCompatActivity {
    private TextView tvUpdatePassEmail;
    private EditText etUpdatePassword, etUpdateConfirmPassword;
    private ImageView ivUpPassShowHide, ivUpConPassShowHide;
    private Button btnUpdatePassword;
    private boolean showPass = true, showPass1 = true;
    private MaterialDialog materialDialog;
    private NetworkUtils networkUtils;
    private DeviceResourceManager resourceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        tvUpdatePassEmail = findViewById(R.id.tvUpdatePassEmail);
        etUpdatePassword = findViewById(R.id.etUpdatePassword);
        etUpdateConfirmPassword = findViewById(R.id.etUpdateConfirmPassword);
        ivUpPassShowHide = findViewById(R.id.ivUpPassShowHide);
        ivUpConPassShowHide = findViewById(R.id.ivUpConPassShowHide);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Update Password");
        resourceManager = new DeviceResourceManager(UpdatePasswordActivity.this);
        networkUtils = new NetworkUtils(this);
        tvUpdatePassEmail.setText(resourceManager.getDataFromSharedPref(Config.EMAIL_ADDRESS));
        ivUpPassShowHide.setOnClickListener(view -> {
            if (showPass) {
                ivUpPassShowHide.setBackgroundResource(R.drawable.ic_invisible);
                etUpdatePassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                etUpdatePassword.setSelection(etUpdatePassword.getText().length());
                showPass = false;
            } else {
                ivUpPassShowHide.setBackgroundResource(R.drawable.ic_visibility);
                etUpdatePassword.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etUpdatePassword.setSelection(etUpdatePassword.getText().length());
                showPass = true;
            }

        });
        ivUpConPassShowHide.setOnClickListener(view -> {
            if (showPass1) {
                ivUpConPassShowHide.setBackgroundResource(R.drawable.ic_invisible);
                etUpdateConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                etUpdateConfirmPassword.setSelection(etUpdateConfirmPassword.getText().length());
                showPass1 = false;
            } else {
                ivUpConPassShowHide.setBackgroundResource(R.drawable.ic_visibility);
                etUpdateConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etUpdateConfirmPassword.setSelection(etUpdateConfirmPassword.getText().length());
                showPass1 = true;
            }
        });

        btnUpdatePassword.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(etUpdatePassword.getText().toString())) {
                if (etUpdatePassword.getText().toString().length() >= 8) {
                    if (!TextUtils.isEmpty(etUpdateConfirmPassword.getText().toString())) {
                        if (etUpdateConfirmPassword.getText().toString().length() >= 8) {
                            if (etUpdatePassword.getText().toString().equals(etUpdateConfirmPassword.getText().toString())) {
                                doUpdatePassword(etUpdatePassword.getText().toString().trim());
                            } else {
                                etUpdatePassword.setError("Please enter match password");
                                etUpdatePassword.requestFocus();
                                etUpdateConfirmPassword.setError("Please enter match password");
                                etUpdateConfirmPassword.requestFocus();
                            }
                        } else {
                            etUpdateConfirmPassword.setError("Password must contains, minimum 8 characters.");
                            etUpdateConfirmPassword.requestFocus();
                        }
                    } else {
                        etUpdateConfirmPassword.setError("Please enter confirm password");
                        etUpdateConfirmPassword.requestFocus();
                    }
                } else {
                    etUpdatePassword.setError("Password must contains, minimum 8 characters.");
                    etUpdatePassword.requestFocus();

                }
            } else {
                etUpdatePassword.setError("Please enter password");
                etUpdatePassword.requestFocus();
            }


        });

    }

    private void doUpdatePassword(String password) {
        materialDialog = new MaterialDialog.Builder(this)
                .content("Please Wait...")
                .cancelable(false)
                .progress(true, 0)
                .show();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", resourceManager.getDataFromSharedPref("USER_ID"));
            jsonBody.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.UpdatePassword, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                Log.w("result", result);
                materialDialog.dismiss();
                try {
                    Log.v("dip", "Login Response : " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String response = jsonObject.optString("response");
                    if (response.equalsIgnoreCase("true")) {
                        resourceManager.clearSharedPref("USER_ID");
                        resourceManager.clearSharedPref(Config.EMAIL_ADDRESS);
                        startActivity(new Intent(UpdatePasswordActivity.this, LoginActivity.class));
                        finish();
                        Toast.makeText(UpdatePasswordActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UpdatePasswordActivity.this, "error", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {

                materialDialog.dismiss();
                Toast.makeText(UpdatePasswordActivity.this, result, Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(UpdatePasswordActivity.this, LoginActivity.class));
        resourceManager.clearSharedPref("USER_ID");
        resourceManager.clearSharedPref(Config.EMAIL_ADDRESS);
        finish();
    }
}
