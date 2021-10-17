package com.video.tamas.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.video.tamas.R;
import com.video.tamas.Utils.Common;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;
import com.video.tamas.Utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity {
    TextView tvRegister, forgot_textview;
    EditText etUsername, etPassword;
    Button btnLogin;
    String userName, password;
    NetworkUtils networkUtils;
    DeviceResourceManager resourceManager;
    MaterialDialog materialDialog;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    ImageView ivPassShowHide;
    boolean showPass = true;
    private FirebaseAuth mAuth;
    private Button loginButton, signInButton;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInOptions gso;
    private String selectedUserType;
    private FirebaseUser googleUser, fbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        signInButton = findViewById(R.id.sign_in_button);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        networkUtils = new NetworkUtils(this);
        resourceManager = new DeviceResourceManager(this);
        tvRegister = findViewById(R.id.tvRegister);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        ivPassShowHide = findViewById(R.id.ivPassShowHide);
        forgot_textview = findViewById(R.id.forgot_textview);
        btnLogin = findViewById(R.id.btnLogin);
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
        btnLogin.setOnClickListener(v -> doValidation());

//btnLogin.performClick();
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            finish();
        });
        forgot_textview.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            finish();
        });

        release_hashFromSHA1("0A:39:17:91:E9:66:E2:34:20:A3:FB:F3:19:0A:1A:F3:48:68:B8:C9");


        signInButton.setOnClickListener(view -> signIn());
        loginButton.setOnClickListener(view -> {
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void release_hashFromSHA1(String sha1) {
        String[] arr = sha1.split(":");
        byte[] byteArr = new byte[arr.length];

        for (int i = 0; i < arr.length; i++) {
            byteArr[i] = Integer.decode("0x" + arr[i]).byteValue();
        }
        Log.e(" ", " --------------------------------------------------------------------------------------------------- ");
        Log.e("hash :  ", Base64.encodeToString(byteArr, Base64.NO_WRAP));
        Log.e(" ", " --------------------------------------------------------------------------------------------------- ");
    }


    public void doValidation() {
        //etUsername.setText("demo@gmail.com");
        //etPassword.setText("12345678");
        userName = StringUtils.getString(etUsername);
        password = StringUtils.getString(etPassword);
        if (StringUtils.isValidForm(userName, password)) {
            doLogin();
//            String toServerUnicodeEncoded = StringEscapeUtils.escapeJava(userName);
//            Log.w("commentMessage",toServerUnicodeEncoded);


        } else {
            Toast.makeText(this, "Please give all details.", Toast.LENGTH_SHORT).show();
        }
    }

    public void doLogin() {
        materialDialog = new MaterialDialog.Builder(this)
                .content("Login user ...")
                .cancelable(false)
                .progress(true, 0)
                .show();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("usernameEmail", userName);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.login, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                Log.w("result", result);
                materialDialog.dismiss();
                try {
                    Log.v("dip", "Login Response : " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String success = jsonObject.optString("status");
                    String loginType = jsonObject.optString("logintype");
                    String message = jsonObject.optString("message");
                    if (success.equalsIgnoreCase("success")) {
                        if (loginType.equals("1")) {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            String userId = jsonObject1.optString("user_id");
                            String userName = jsonObject1.optString("username");
                            String emailAddress = jsonObject1.optString("email");
                            resourceManager.addToSharedPref(Config.USER_NAME, userName);
                            resourceManager.addToSharedPref(Config.USER_ID, userId);
                            resourceManager.addToSharedPref(Config.LoginType, loginType);
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        } else {
                            materialDialog = new MaterialDialog.Builder(LoginActivity.this)
                                    .content(message)
                                    .positiveText("Ok")
                                    .cancelable(false)
                                    .onPositive((dialog, which) -> {
                                        materialDialog.dismiss();
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                        finish();
                                    })
                                    .show();
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {

                materialDialog.dismiss();
                Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resulrCode, Intent data) {
        super.onActivityResult(requestCode, resulrCode, data);
        callbackManager.onActivityResult(requestCode, resulrCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            if (data != null) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                if (task.isSuccessful()) {
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        googleUser = mAuth.getCurrentUser();
                        if (googleUser.getEmail() != null) {
                            checkEmail(googleUser.getEmail(), Common.getDeviceId(), "Android", googleUser.getDisplayName());
                        } else {
                            Toast.makeText(LoginActivity.this, "google email:" + googleUser.getEmail(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }

                });
    }


    private void handleFacebookAccessToken(AccessToken token) {

        GraphRequest graphRequest = GraphRequest.newMeRequest(token, (object, response) -> {

            try {
                String name = object.getString("first_name");
                String last_name = object.getString("last_name");
                String email = object.getString("email");
                String fbb_id = object.getString("id");
                resourceManager.addToSharedPref("FB_ID", fbb_id);
                checkEmail(email, Common.getDeviceId(), "Android", name);

            } catch (JSONException e) {
                Toast.makeText(LoginActivity.this, "Email Id Not Found in Facebook...", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        Bundle params = new Bundle();
        params.putString("fields", "first_name,last_name,email,id");
        graphRequest.setParameters(params);
        graphRequest.executeAsync();

    }

//    private void handleFacebookAccessToken(AccessToken token) {
////        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
////        mAuth.signInWithCredential(credential)
////                .addOnCompleteListener(this, task -> {
////                    if (task.isSuccessful()) {
////                        fbUser = mAuth.getCurrentUser();
////                        if (fbUser.getEmail() != null) {
////                            checkEmail(fbUser.getEmail(), Common.getDeviceId(), "Android", fbUser.getDisplayName());
////                        } else {
////                            Toast.makeText(LoginActivity.this, "fb email:" + fbUser.getEmail(), Toast.LENGTH_SHORT).show();
////                        }
////                    } else {
////                        Toast.makeText(getApplicationContext(), "Authentication failed." + task.getException(),
////                                Toast.LENGTH_SHORT).show();
////                    }
////                });
//        /**
//         Creating the GraphRequest to fetch user details
//         1st Param - AccessToken
//         2nd Param - Callback (which will be invoked once the request is successful)
//         **/
//        //OnCompleted is invoked once the GraphRequest is successful
//        GraphRequest request = GraphRequest.newMeRequest(token, (JSONObject object, GraphResponse response) -> {
//            if (object != null) {
//                try {
//                    String name = object.getString("name");
//                    String email1 = object.getString("email");
//                    if (email1 != null) {
//                        checkEmail(email1, Common.getDeviceId(), "Android", name);
//                    } else {
//                        Toast.makeText(LoginActivity.this, "fb email:" + email1, Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Toast.makeText(LoginActivity.this, "null", Toast.LENGTH_SHORT).show();
//            }
//        });
//        // We set parameters to the GraphRequest using a Bundle.
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id,name,email,picture.width(200)");
//        request.setParameters(parameters);
//        // Initiate the GraphRequest
//        request.executeAsync();
//    }

    private void checkEmail(String email, String deviceToken, String deviceType, String userName) {
        materialDialog = new MaterialDialog.Builder(this)
                .content("Login user ...")
                .cancelable(false)
                .progress(true, 0)
                .show();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.CheckEmail, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                Log.w("result", result);
                materialDialog.dismiss();
                try {
                    Log.v("dip", "Login Response : " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String success = jsonObject.optString("status");
                    String loginType = jsonObject.optString("logintype");
                    String message = jsonObject.optString("message");
                    if (success.equalsIgnoreCase("success")) {
                        if (loginType.equals("1")) {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            String userId = jsonObject1.optString("user_id");
                            String userName = jsonObject1.optString("username");
                            String emailAddress = jsonObject1.optString("email");
                            resourceManager.addToSharedPref(Config.USER_NAME, userName);
                            resourceManager.addToSharedPref(Config.USER_ID, userId);
                            resourceManager.addToSharedPref(Config.LoginType, loginType);
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        } else {
                            materialDialog = new MaterialDialog.Builder(LoginActivity.this)
                                    .content(message)
                                    .positiveText("Ok")
                                    .cancelable(false)
                                    .onPositive((dialog, which) -> {
                                        materialDialog.dismiss();
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                        finish();
                                    })
                                    .show();
                        }

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Info");
                        builder.setMessage("You are new user seen. so you are want to registration?");
                        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                            getRegistration(email, deviceToken, deviceType, userName);
                            dialogInterface.cancel();
                        }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());

                        AlertDialog alertDialog = builder.create();
                        if (!alertDialog.isShowing())
                            alertDialog.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                materialDialog.dismiss();
                Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();
            }

        });
    }

    private void getRegistration(String email, String deviceToken, String deviceType, String userName) {
        RadioButton rbIndividual1, rbProducer1;
        RadioGroup rgUserType1;

        View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.firebase_login_layout, null);
        rgUserType1 = view.findViewById(R.id.rgUserType1);
        rbIndividual1 = view.findViewById(R.id.rbIndividual1);
        rbProducer1 = view.findViewById(R.id.rbProducer1);
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(view);
        builder.setTitle("Please choose registration type");
        rgUserType1.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbIndividual:
                    selectedUserType = "1";
                    rbProducer1.setChecked(false);
                    break;
                case R.id.rbProducer:
                    selectedUserType = "2";
                    rbIndividual1.setChecked(false);
                    break;
            }
        });

        builder.setPositiveButton("Submit", (dialogInterface, i) -> {
            doReg(email, deviceType, deviceToken, userName, selectedUserType);
            dialogInterface.cancel();
        }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialog.show();

    }

    private void doReg(String email, String deviceType, String deviceToken, String userName, String selectedUserType) {
        materialDialog = new MaterialDialog.Builder(this)
                .content("Registering user ...")
                .cancelable(false)
                .progress(true, 0)
                .show();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email_add ", email);
            jsonBody.put("devicetoken", deviceToken);
            jsonBody.put("registertype ", selectedUserType);
            jsonBody.put("devicetype", deviceType);
            jsonBody.put("username", userName);
            Log.wtf("jsonoby", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.RegFB, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                Log.w("result", result);
                materialDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (status.equalsIgnoreCase("success")) {
                        materialDialog = new MaterialDialog.Builder(LoginActivity.this)
                                .content(message)
                                .positiveText("Ok")
                                .cancelable(false)
                                .onPositive((dialog, which) -> {
                                    materialDialog.dismiss();
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    finish();
                                })
                                .show();
                    } else {
                        materialDialog = new MaterialDialog.Builder(LoginActivity.this)
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }
}
