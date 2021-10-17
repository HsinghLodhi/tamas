package com.video.tamas.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.video.tamas.R;
import com.video.tamas.Utils.AppAlerts;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.LocaleManager;

public class SettingActivity extends AppCompatActivity {
    TextView tvChangeLanguage;
    String selectedLanguage = "hi";
    DeviceResourceManager resourceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        resourceManager = new DeviceResourceManager(this);
        findViewById(R.id.tvLogout)
                .setOnClickListener(view -> new AppAlerts().showAlertWithAction(SettingActivity.this, "Confirm!", "Do you want to Logout?", "Yes", "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resourceManager.clearSharedPref(Config.USER_ID);
                        resourceManager.clearSharedPref(Config.USER_NAME);
                        startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                        FirebaseAuth.getInstance().signOut();
                        logOut();
                        finish();

                    }
                }, true));
        tvChangeLanguage = findViewById(R.id.tvChangeLanguage);
        tvChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguage();

            }
        });

        findViewById(R.id.tvPrivacyPolicy).setOnClickListener(view -> {
            Intent privacyIntent = new Intent(SettingActivity.this, AboutUsAndPrivacyPolicyActivity.class);
            privacyIntent.putExtra("PAGE_NAME", "Privacy Policy");
            startActivity(privacyIntent);
            finish();
        });
        findViewById(R.id.tvTermsAndCondition).setOnClickListener(view -> {
            Intent privacyIntent = new Intent(SettingActivity.this, AboutUsAndPrivacyPolicyActivity.class);
            privacyIntent.putExtra("PAGE_NAME", "Terms And Condition");
            startActivity(privacyIntent);
            finish();
        });

        findViewById(R.id.tvSupport).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            String[] recipients = {"support@tamas.in"};
            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
            intent.setType("text/html");
            intent.setPackage("com.google.android.gm");
            startActivity(Intent.createChooser(intent, "Send mail"));
        });
    }

    private void showLanguage() {
        final CharSequence[] items = {"English", "Hindi"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Language");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("English")) {
                    selectedLanguage = "en";
                    resourceManager.addToSharedPref(Config.AppLanguage, selectedLanguage);
                    LocaleManager.setLocale(SettingActivity.this, selectedLanguage);
                    recreate();
                } else if (items[item].equals("Hindi")) {
                    selectedLanguage = "hi";
                    resourceManager.addToSharedPref(Config.AppLanguage, selectedLanguage);
                    LocaleManager.setLocale(SettingActivity.this, selectedLanguage);
                    recreate();
                }
            }
        });
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                onBackPressed();
                startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingActivity.this, HomeActivity.class));
        finish();
    }


    private void logOut() {
        if (AccessToken.getCurrentAccessToken() != null) {
            String fb_id = resourceManager.getDataFromSharedPref("FB_ID");
            GraphRequest delPermRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + fb_id + "/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                    if (graphResponse != null) {
                        FacebookRequestError error = graphResponse.getError();
                        if (error != null) {

                        } else {
                        }
                    }
                }
            });
            Log.d("Logout FB", "Executing revoke permissions with graph path" + delPermRequest.getGraphPath());
            delPermRequest.executeAsync();
        }
        GoogleSignInAccount google_acc = GoogleSignIn.getLastSignedInAccount(SettingActivity.this);
        if (google_acc != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getResources().getString(R.string.web_client_id))
                    .requestEmail().build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(SettingActivity.this, gso);
            mGoogleSignInClient.signOut();
        } else {
            Log.e("------", "NO GOOGLE SIGN IN HERE");
        }
    }
}