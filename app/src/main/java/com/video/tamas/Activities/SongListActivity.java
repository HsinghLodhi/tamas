package com.video.tamas.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.video.tamas.Adapters.SongListRecyclerAdapter;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.SongListModel;
import com.video.tamas.R;
import com.video.tamas.Utils.AndroidMultiPartEntity;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.NetworkUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class SongListActivity extends AppCompatActivity implements SongListRecyclerAdapter.OnMediaPlayerListener {
    private static final String TAG = SongListActivity.class.getSimpleName();
    private NetworkUtils networkUtils;
    RecyclerView rvSongList;
    FloatingActionButton fabUploadSong;
    int REQUEST_CODE = 1001;
    String selectedImagePath;
    long totalSize = 0;
    TextView tvPercentage;
    ProgressBar progressBar;
    DeviceResourceManager resourceManager;
    private String userId;
    TextView tvErrorMessage;
    private boolean play = false;
    private MediaPlayer mediaPlayer;
    private LazyLoader lazyLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvSongList = findViewById(R.id.rvSongList);
        lazyLoader = findViewById(R.id.lazyLoaderSongList);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        resourceManager = new DeviceResourceManager(this);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        networkUtils = new NetworkUtils(this);
        fabUploadSong = findViewById(R.id.fabUploadSong);
        fabUploadSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(userId)) {
                    Log.wtf("userid", "userid");
                    Intent audioIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(audioIntent, REQUEST_CODE);
                } else {
                    Log.wtf("userid", "notuserid");
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }

            }
        });
        getSongList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            Uri selectedImageUri = data.getData();
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = getContentResolver().query(selectedImageUri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();

            selectedImagePath = cursor.getString(column_index);
            //selectedImagePath = selectedImagePath.replace(" ", "");
            System.out.println("path slelcted image path" + selectedImagePath);
            new SongListActivity.UploadFileToServer().execute();
        }
    }

    public void getSongList() {
        networkUtils.getDataVolley(Config.MethodName.getSongList, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("response", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    if (status.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        try {
                            List<SongListModel> songListModelList = ParcingUtils.parseSongListModelList(SongListActivity.this, jsonArray);
                            Collections.reverse(songListModelList);
                            SongListRecyclerAdapter songListRecyclerAdapter = new SongListRecyclerAdapter(SongListActivity.this, songListModelList);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SongListActivity.this);
                            rvSongList.setLayoutManager(layoutManager);
                            rvSongList.setAdapter(songListRecyclerAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        tvErrorMessage.setVisibility(View.VISIBLE);

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

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            Dialog settingsDialog = new Dialog(SongListActivity.this);
            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.dialog_progress
                    , null));
            tvPercentage = settingsDialog.findViewById(R.id.txtPercentage);
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
            tvPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.MethodName.uploadSong);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(selectedImagePath);
                String fileName = sourceFile.getName().replace(".mp3", "");
                // Adding file data to http body
                entity.addPart("file_name", new FileBody(sourceFile));
                entity.addPart("song_title", new StringBody(fileName));
                entity.addPart("user_id", new StringBody(userId));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                Log.wtf("params", String.valueOf(entity));

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

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
    public boolean onSupportNavigateUp() {
        finish();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SongListRecyclerAdapter.setOnMediaPlayerListener(this);
    }

    @Override
    public void onPlay(boolean play, MediaPlayer player) {
        this.play = play;
        mediaPlayer = player;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

        }
    }
}
