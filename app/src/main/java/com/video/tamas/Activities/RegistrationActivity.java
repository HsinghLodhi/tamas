package com.video.tamas.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.video.tamas.R;
import com.video.tamas.Utils.Common;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.NetworkUtils;
import com.video.tamas.Utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class RegistrationActivity extends AppCompatActivity {
    EditText etUsername, etEmail, etMobile, etPassword, etConfirmPassword;
    Button btnRegister;
    String userName, emailAddress, mobileNo, password, confirmPassword;
    NetworkUtils networkUtils;
    private TextView tvCheckTermConditionReg;
    RadioButton rbIndividual, rbProducer;
    RadioGroup rgUserType;
    String selectedUserType = "1";
    private MaterialDialog materialDialog;
    int randomNumber;
    ImageView ivBackButton, ivPassShowHide, ivConPassShowHide;
    boolean showPass = true, showPass1 = true;
    private CheckBox cbTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        networkUtils = new NetworkUtils(this);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmailAddress);
        etMobile = findViewById(R.id.etMobileNo);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivPassShowHide = findViewById(R.id.ivPassShowHide);
        ivConPassShowHide = findViewById(R.id.ivConPassShowHide);
        rgUserType = findViewById(R.id.rgUserType);
        rbIndividual = findViewById(R.id.rbIndividual);
        rbProducer = findViewById(R.id.rbProducer);
        cbTerms = findViewById(R.id.cbTerms);
        ivBackButton = findViewById(R.id.ivBackButton);
        btnRegister = findViewById(R.id.btnRegister);
        tvCheckTermConditionReg = findViewById(R.id.tvCheckTermConditionReg);
        tvCheckTermConditionReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tcIntent = new Intent(RegistrationActivity.this, AboutUsAndPrivacyPolicyActivity.class);
                tcIntent.putExtra("PAGE_NAME", "Check Terms And Condition");
                startActivity(tcIntent);
                finish();
            }
        });
        ivPassShowHide.setOnClickListener(view -> {
            if (showPass) {
                ivPassShowHide.setBackgroundResource(R.drawable.ic_invisible);
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                etPassword.setSelection(etPassword.getText().length());
                showPass = false;
            } else {
                ivPassShowHide.setBackgroundResource(R.drawable.ic_visibility);
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etPassword.setSelection(etPassword.getText().length());
                showPass = true;
            }

        });
        ivConPassShowHide.setOnClickListener(view -> {
            if (showPass1) {
                ivConPassShowHide.setBackgroundResource(R.drawable.ic_invisible);
                etConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                etConfirmPassword.setSelection(etConfirmPassword.getText().length());
                showPass1 = false;
            } else {
                ivConPassShowHide.setBackgroundResource(R.drawable.ic_visibility);
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etConfirmPassword.setSelection(etConfirmPassword.getText().length());
                showPass1 = true;
            }
        });
        ivBackButton.setOnClickListener(view -> {
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            finish();
        });
        Random r = new Random();
        randomNumber = r.nextInt(10000);
        Log.wtf("randomcode", String.valueOf(randomNumber));

        rgUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbIndividual:
                        selectedUserType = "1";
                        rbProducer.setChecked(false);
                        break;
                    case R.id.rbProducer:
                        selectedUserType = "2";
                        rbIndividual.setChecked(false);
                        break;
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dValidation();
            }
        });

    }

    public void dValidation() {
        userName = StringUtils.getString(etUsername);
        emailAddress = StringUtils.getString(etEmail);
        mobileNo = StringUtils.getString(etMobile);
        password = StringUtils.getString(etPassword);
        confirmPassword = StringUtils.getString(etConfirmPassword);
        if (!TextUtils.isEmpty(emailAddress) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword) && !TextUtils.isEmpty(userName)) {
            if (!confirmPassword.equals(password)) {
                materialDialog = new MaterialDialog.Builder(this)
                        .content("Password is Not Matching.")
                        .positiveText("Ok")
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                materialDialog.dismiss();
                            }
                        })
                        .show();
                return;
            }
            if (password.length() < 8) {
                materialDialog = new MaterialDialog.Builder(this)
                        .content("Password must contains, minimum 8 characters.")
                        .positiveText("Ok")
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                materialDialog.dismiss();
                            }
                        })
                        .show();
                return;
            }
            if (userName.length() < 6) {
                materialDialog = new MaterialDialog.Builder(this)
                        .content("username must contains, minimum 6 characters.")
                        .positiveText("Ok")
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                materialDialog.dismiss();
                            }
                        })
                        .show();
                return;
            }
            if (!cbTerms.isChecked()) {
                Toast.makeText(RegistrationActivity.this, "Please agree Terms and Conditions", Toast.LENGTH_SHORT).show();
                return;
            }
            doRegistration();
        } else {
            Toast.makeText(this, "Fields cannot be left blank", Toast.LENGTH_LONG).show();
        }


    }

    public void doRegistration() {
        materialDialog = new MaterialDialog.Builder(this)
                .content("Registering user ...")
                .cancelable(false)
                .progress(true, 0)
                .show();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", userName + String.valueOf(randomNumber));
            jsonBody.put("mobile", mobileNo);
            jsonBody.put("password", password);
            jsonBody.put("email", emailAddress);
            jsonBody.put("devicetype", "android");
            jsonBody.put("registertype", selectedUserType);
            jsonBody.put("devicetoken", Common.getDeviceId());
            Log.wtf("jsonoby", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.Register, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                Log.w("result", result);
                materialDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (status.equalsIgnoreCase("success")) {
                        materialDialog = new MaterialDialog.Builder(RegistrationActivity.this)
                                .content(message)
                                .positiveText("Ok")
                                .cancelable(false)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                        materialDialog.dismiss();
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                        finish();
                                    }
                                })
                                .show();
                    } else {
                        materialDialog = new MaterialDialog.Builder(RegistrationActivity.this)
                                .content(message)
                                .positiveText("Ok")
                                .cancelable(false)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                        materialDialog.dismiss();
                                    }
                                })
                                .show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                materialDialog.dismiss();
            }

        });
    }
}
