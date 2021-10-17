package com.video.tamas.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.video.tamas.R;
import com.video.tamas.Utils.AndroidMultiPartEntity;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

public class TalentVideoUploadActivity extends AppCompatActivity {
    // LogCat tag
    private static final String TAG = TalentVideoUploadActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private VideoView vidPreview;
    private Button btnUpload, btnDraft;
    long totalSize = 0;
    private SwitchCompat btnSwitch;
    private TextView tvCommentStatus;
    private EditText etDescription;
    private String commentStatus = "1", draftStatus = "0", songId;
    String description;
    private MaterialDialog materialDialog;
    DeviceResourceManager resourceManager;
    private String userId, subcategoryId;
    String toServerUnicodeEncoded;
    Dialog settingsDialog;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        resourceManager = new DeviceResourceManager(this);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnDraft = (Button) findViewById(R.id.btnDraft);
        vidPreview = (VideoView) findViewById(R.id.videoPreview);
        btnSwitch = findViewById(R.id.btnSwitch);
        tvCommentStatus = findViewById(R.id.tvCommentStatus);
        etDescription = findViewById(R.id.etDescription);
        Intent i = getIntent();
        // image or video path that is captured in previous activity
        filePath = i.getStringExtra("filePath");
        subcategoryId = i.getStringExtra("subcategoryId");
        Log.w("filepath", filePath);
        Log.w("subcategoryId", subcategoryId);
        vidPreview.setVisibility(View.VISIBLE);
        vidPreview.setVideoPath(filePath);
        // start playing
        vidPreview.start();
        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tvCommentStatus.setText("Comment On");
                    commentStatus = "0";
                    Log.wtf("commentStatus", commentStatus);

                } else {
                    tvCommentStatus.setText("Comment Off");
                    commentStatus = "1";
                    Log.wtf("commentStatus", commentStatus);
                }

            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // uploading the file to server
                draftStatus = "0";

                description = etDescription.getText().toString();
                toServerUnicodeEncoded = StringEscapeUtils.escapeJava(description);
                Log.wtf("description", description);
                new UploadFileToServer().execute();

            }
        });
        btnDraft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // uploading the file to server
                /* draftStatus = "1";*/
                Log.wtf("draftStatus", draftStatus);
                description = etDescription.getText().toString();
                toServerUnicodeEncoded = StringEscapeUtils.escapeJava(description);
                new UploadFileToServer().execute();
            }
        });
    }


    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            settingsDialog = new Dialog(TalentVideoUploadActivity.this);
            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.dialog_progress
                    , null));
            txtPercentage = settingsDialog.findViewById(R.id.txtPercentage);
            progressBar = settingsDialog.findViewById(R.id.progressBar);
            progressBar.setProgress(0);   // Main Progress
            progressBar.setSecondaryProgress(100); // Secondary Progress
            progressBar.setMax(100); // Maximum Progress
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_circle));
            settingsDialog.show();
            settingsDialog.setCancelable(false);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);
            progressBar.setIndeterminate(true);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.tamas.in/api/VideoApp/mobileapi/service_video.php?servicename=videoupload");

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart("file_name", new FileBody(sourceFile));
                entity.addPart("description", new StringBody(toServerUnicodeEncoded));
                entity.addPart("user_id", new StringBody(userId));
                entity.addPart("categories_id", new StringBody("2"));
                entity.addPart("sub_category", new StringBody(subcategoryId));
                entity.addPart("song_id", new StringBody("0"));
                entity.addPart("draft", new StringBody(draftStatus));
                // Extra parameters if you want to pass to server
                entity.addPart("comment_status",
                        new StringBody(commentStatus));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                Log.wtf("uploadResponse", entity.toString());

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                Log.wtf("uploadResponse", r_entity.toString());
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            // showing the server response in an alert dialog
            showAlert(result);
            progressBar.setVisibility(View.GONE);
            settingsDialog.dismiss();
            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Upload Status")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        materialDialog = new MaterialDialog.Builder(TalentVideoUploadActivity.this)
                .content("Are you sure you want to discard video ??")
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