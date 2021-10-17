package com.video.tamas.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
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
import com.video.tamas.Adapters.SongTabAdapter;
import com.video.tamas.Adapters.TalentCategoryListAdapter;
import com.video.tamas.Fragments.HomeFragment;
import com.video.tamas.Fragments.NotificationFragment;
import com.video.tamas.Fragments.ProfileFragment;
import com.video.tamas.Fragments.SearchFragment;
import com.video.tamas.Fragments.TalentFragment;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.TalentCategoryModel;
import com.video.tamas.R;
import com.video.tamas.Utils.AppAlerts;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;
import com.video.tamas.Utils.ProgressRequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends BaseActivity implements ProgressRequestBody.UploadCallbacks, TalentFragment.OnTalentSelectListener, TalentCategoryListAdapter.OnMakeWithTalentCategoryListener {
    FloatingActionButton btnMakeVideo;
    private MaterialDialog materialDialog;
    DeviceResourceManager resourceManager;
    public static ProgressRequestBody.UploadCallbacks mVideoUploadProgressHandler = null;
    private String userId, appLanguage;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 88888;
    LinearLayout llUploadProgress;
    ProgressBar mUploadVideoProgressBar;
    TextView tvShowProgress;
    boolean selected = false;
    NetworkUtils networkUtils;
    private List<TalentCategoryModel> talentCategoryModelList;
    private BottomNavigationView navView;
    private boolean status;
    private LazyLoader lazyLoader;
    private MediaPlayer mediaPlayer = null;
    private boolean play = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        lazyLoader = findViewById(R.id.lazyLoaderHomeActivity);
        mVideoUploadProgressHandler = HomeActivity.this;
        resourceManager = new DeviceResourceManager(this);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        appLanguage = resourceManager.getDataFromSharedPref(Config.AppLanguage, "");
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        llUploadProgress = (LinearLayout) findViewById(R.id.llVideoProgress);
        mUploadVideoProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvShowProgress = (TextView) findViewById(R.id.tvShowProgress);
        llUploadProgress.setVisibility(View.GONE);
        networkUtils = new NetworkUtils(HomeActivity.this);
        btnMakeVideo = findViewById(R.id.btnMakeVideo);
        loadFragment(new HomeFragment());
        if (!TextUtils.isEmpty(userId)) {
            checkUser(userId);
        }
        btnMakeVideo.setOnClickListener(v -> {
            stopMediaPlayer();
            if (!TextUtils.isEmpty(userId)) {
                Log.wtf("userid", "userid");
                if (selected) {
                    getCategory();
                } else {
                    startActivity(new Intent(getApplicationContext(), EntertainmentVideoMakeActivity.class)
                            .putExtra("from", "entertainment")
                            .putExtra("subcategoryId", "0"));
                }
            } else {
                Log.wtf("userid", "notuserid");
                //startActivity(new Intent(getApplicationContext(), EntertainmentVideoMakeActivity.class));
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }


        });
    }

    private void showInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Info").setMessage("You are blocked by Tamas").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //loadFragment(new HomeFragment());
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    stopMediaPlayer();
                    fragment = new HomeFragment();
                    break;
                case R.id.navigation_search:
                    stopMediaPlayer();
                    selected = false;
                    fragment = new SearchFragment();
                    break;
                case R.id.navigation_notifications:
                    stopMediaPlayer();
                    selected = false;
                    if (!TextUtils.isEmpty(userId)) {
                        Log.wtf("userid", "userid");
                        fragment = new NotificationFragment();
                    } else {
                        Log.wtf("userid", "notuserid");
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }

                    break;
                case R.id.navigation_me:
                    stopMediaPlayer();
                    selected = false;
                    if (!TextUtils.isEmpty(userId)) {
                        Log.wtf("userid", "userid");
                        fragment = new ProfileFragment();
                    } else {
                        Log.wtf("userid", "notuserid");
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                    break;
            }
            return loadFragment(fragment);
        }
    };


    public boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        stopMediaPlayer();
        int seletedItemId = navView.getSelectedItemId();
        if (R.id.navigation_home != seletedItemId) {
            navView.setSelectedItemId(R.id.navigation_home);
        } else {
            materialDialog = new MaterialDialog.Builder(HomeActivity.this)
                    .content("Are you sure you want to Exit")
                    .contentColor(getResources().getColor(R.color.colorWhite))
                    .backgroundColor(getResources().getColor(R.color.colorTableGrey))
                    .positiveText("Ok")
                    .negativeText("Cancel")
                    .cancelable(false)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                            materialDialog.dismiss();
                            finish();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                            materialDialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void stopMediaPlayer() {
        if (SongTabAdapter.mp != null) {
            if (SongTabAdapter.mp.isPlaying()) {
                SongTabAdapter.mp.stop();
            }
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        // request camera permission if it has not been grunted.
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(HomeActivity.this, "camera permission has been grunted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "[WARN] camera permission is not grunted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
        mediaPlayer = SongTabAdapter.mp;
        TalentFragment.setOnTalentSelectListener(this);
        TalentCategoryListAdapter.setOnMakeWithTalentCategoryListener(this);
        if (SongTabAdapter.mp != null) {
            if (!SongTabAdapter.mp.isPlaying()) {
                SongTabAdapter.mp.start();
            }
        }
    }

    @Override
    public void onTalentSelect(boolean select) {
        selected = select;
    }

    @Override
    public void onMakeWithTalentCategory(boolean makeType, int position) {
        TalentCategoryModel talentCategoryModel = talentCategoryModelList.get(position);
        if (talentCategoryModel.getCatId().equals("4")) {
            Intent intent1 = new Intent(HomeActivity.this, WriterListUploadActivity.class);
            intent1.putExtra("subcategoryId", talentCategoryModel.getCatId());
            startActivity(intent1);
        } else {
            Intent intent = new Intent(getApplicationContext(), EntertainmentVideoMakeActivity.class);
            intent.putExtra("from", "category");
            intent.putExtra("subcategoryId", talentCategoryModel.getCatId());
            startActivity(intent);
            resourceManager.addToSharedPref("IS_TALENT_PAGE", true);
        }
    }

    @Override
    protected void onPause() {
        //Toast.makeText(this, "Activity paused", Toast.LENGTH_SHORT).show();
        Intent in = new Intent("STOP_PLAYING");
        in.putExtra("status", "1");
        LocalBroadcastManager.getInstance(HomeActivity.this).sendBroadcast(in);
        super.onPause();
        if (SongTabAdapter.mp != null) {
            if (SongTabAdapter.mp.isPlaying()) {
                SongTabAdapter.mp.pause();
            }
        }
    }

    @Override
    protected void onStop() {
        //Toast.makeText(this, "Activity stop", Toast.LENGTH_SHORT).show();
        super.onStop();
    }

    @Override
    public void onProgressUpdate(int percentage) {
        Log.v("serviceUpload", "upload progress : " + percentage);
        llUploadProgress.setVisibility(View.VISIBLE);
        mUploadVideoProgressBar.setProgress(percentage);
        tvShowProgress.setText("" + percentage + "%");
    }

    @Override
    public void onError() {
        Log.v("serviceUpload", "upload error");
    }

    @Override
    public void onFinish() {
        Log.v("serviceUpload", "upload completed");
        llUploadProgress.setVisibility(View.GONE);
        new AppAlerts().showAlertMessage(HomeActivity.this, "Info!", "Video uploded Successfully.");
    }

    @Override
    public void uploadStart() {

        llUploadProgress.setVisibility(View.VISIBLE);
        mUploadVideoProgressBar.setProgress(0);
        tvShowProgress.setText("0%");
        Log.v("serviceUpload", "upload Started");
    }

    public void getCategory() {
        RecyclerView recyclerView;
        View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.talent_category_dialog, null);
        Dialog dialog = new Dialog(HomeActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(getResources().getDisplayMetrics().widthPixels, RecyclerView.LayoutParams.WRAP_CONTENT);

        recyclerView = view.findViewById(R.id.rvTalentCatDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        recyclerView.setHasFixedSize(true);

        lazyLoader.setVisibility(View.VISIBLE);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("category_id", "2");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getSubcategory, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    try {
                        lazyLoader.setVisibility(View.GONE);
                        talentCategoryModelList = ParcingUtils.parseTalentCatModelList(jsonArray);
                        TalentCategoryListAdapter talentCategoryListAdapter = new TalentCategoryListAdapter(HomeActivity.this, talentCategoryModelList, dialog);
                        recyclerView.setAdapter(talentCategoryListAdapter);
                        dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
            }

        });
    }

    private boolean checkUserStatus(String userId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.userStatus, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String response = jsonObject.getString("responce");
                    if (Boolean.parseBoolean(response)) {
                        status = true;
                    } else {
                        status = false;

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {

            }

        });
        return status;
    }

    public boolean checkUser(String userId1) {
        RequestQueue requestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, Config.MethodName.userStatus,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String response = jsonObject.getString("responce");
                            if (Boolean.parseBoolean(response)) {
                                //loadFragment(new HomeFragment());
                            } else {
                                resourceManager.clearSharedPref(Config.USER_ID);
                                resourceManager.clearSharedPref(Config.USER_NAME);
                                FirebaseAuth.getInstance().signOut();
                                logOut();
                                userId = "";
                                showInfoDialog();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId1);
                return params;
            }
        };

        requestQueue.add(jsonObjRequest);
        return status;
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
        GoogleSignInAccount google_acc = GoogleSignIn.getLastSignedInAccount(HomeActivity.this);
        if (google_acc != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getResources().getString(R.string.web_client_id))
                    .requestEmail().build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(HomeActivity.this, gso);
            mGoogleSignInClient.signOut();
        } else {
            Log.e("------", "NO GOOGLE SIGN IN HERE");
        }
    }
}
