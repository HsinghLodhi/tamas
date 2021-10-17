package com.video.tamas.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.video.tamas.Adapters.WriterListRecyclerAdapter;
import com.video.tamas.JsonParsingClass.ParcingUtils;
import com.video.tamas.Models.WriterListModel;
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
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WriterListUploadActivity extends AppCompatActivity {
    NetworkUtils networkUtils;
    RecyclerView rvVideos;
    BottomSheetDialog dialog;
    DeviceResourceManager resourceManager;
    private String userId;
    TextView tvErrorMessage;
    FloatingActionButton btnWriteText;
    String subcategoryId;
    View commentView;
    EditText etCommentMessage, etDescription;
    private LazyLoader lazyLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_list_upload);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnWriteText = findViewById(R.id.btnMakeVideo);
        lazyLoader = findViewById(R.id.lazyLoaderWriterList);
        networkUtils = new NetworkUtils(this);
        rvVideos = findViewById(R.id.rvSongList);
        resourceManager = new DeviceResourceManager(this);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        Intent intent = getIntent();
        subcategoryId = intent.getStringExtra("subcategoryId");
        Log.wtf("subCat", subcategoryId);
        getWriterList();

        btnWriteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentView = getLayoutInflater().inflate(R.layout.writer_list_upload, null);
                dialog = new BottomSheetDialog(WriterListUploadActivity.this);
                dialog.setContentView(commentView);
                dialog.show();
                etCommentMessage = commentView.findViewById(R.id.etCommentMessage);
                etDescription = commentView.findViewById(R.id.etDescription);
                Button btnSendComment = commentView.findViewById(R.id.btnSendComment);
                ImageView btnTextToSpeech = commentView.findViewById(R.id.btnTextToSpeech);
                final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(WriterListUploadActivity.this);
                final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.getDefault());

                mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle bundle) {

                    }

                    @Override
                    public void onBeginningOfSpeech() {

                    }

                    @Override
                    public void onRmsChanged(float v) {

                    }

                    @Override
                    public void onBufferReceived(byte[] bytes) {

                    }

                    @Override
                    public void onEndOfSpeech() {

                    }

                    @Override
                    public void onError(int i) {

                    }

                    @Override
                    public void onResults(Bundle bundle) {
                        //getting all the matches
                        ArrayList<String> matches = bundle
                                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                        //displaying the first match
                        if (matches != null)
                            etCommentMessage.setText(matches.get(0));
                    }

                    @Override
                    public void onPartialResults(Bundle bundle) {

                    }

                    @Override
                    public void onEvent(int i, Bundle bundle) {

                    }
                });
                btnTextToSpeech.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_UP:
                                mSpeechRecognizer.stopListening();
                                etCommentMessage.setHint("You will see input here");
                                break;

                            case MotionEvent.ACTION_DOWN:
                                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                                etCommentMessage.setText("");
                                etCommentMessage.setHint("Listening...");
                                break;
                        }
                        return false;
                    }
                });

                btnSendComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = etCommentMessage.getText().toString();
                        String description = etDescription.getText().toString();
                        if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(description)) {
                            new WriterListUploadActivity.UploadFileToServer().execute();
                        } else {
                            Toast.makeText(WriterListUploadActivity.this, "Please enter text to proceed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero


            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible

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

                            }
                        });


                // Adding file data to http body
                entity.addPart("file_name", new StringBody(""));
                entity.addPart("description", new StringBody(etDescription.getText().toString()));
                entity.addPart("user_id", new StringBody(userId));
                entity.addPart("categories_id", new StringBody("2"));
                entity.addPart("sub_category", new StringBody(subcategoryId));
                entity.addPart("song_id", new StringBody("0"));
                entity.addPart("draft", new StringBody("0"));
                // Extra parameters if you want to pass to server
                entity.addPart("comment_status",
                        new StringBody("0"));
                entity.addPart("writer_text",
                        new StringBody(etCommentMessage.getText().toString()));
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
            Log.e("response", "Response from server: " + result);
            // showing the server response in an alert dialog
            showAlert(result);
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

    public void getWriterList() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            Log.wtf("jsonLike", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.wtf("writerApiCall", Config.MethodName.getWriterList);
        networkUtils.postDataVolley(Config.MethodName.getWriterList, jsonBody, new VolleyCallback() {


            @Override
            public void onSuccessResponse(String result) {
                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("writerResponse", result);
                    Log.wtf("response", jsonObject.toString());
                    String status = jsonObject.optString("result");
                    if (status.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        try {
                            List<WriterListModel> writerListModelList = ParcingUtils.parseWriterListModelList(jsonArray);
                            WriterListRecyclerAdapter songListRecyclerAdapter = new WriterListRecyclerAdapter(WriterListUploadActivity.this, writerListModelList);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(WriterListUploadActivity.this);
                            rvVideos.setLayoutManager(layoutManager);
                            rvVideos.setAdapter(songListRecyclerAdapter);
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

    public void likeVideoApi(String videoId, String position) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("video_id", videoId);
            Log.wtf("jsonLike", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.like, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("likeResponse", jsonObject.toString());
                    String status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (status.equals("success")) {
                        Toast.makeText(WriterListUploadActivity.this, message, Toast.LENGTH_SHORT).show();
                        Log.wtf("likePosition", position);
                        getWriterList();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureResponse(String result) {

            }

        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        getWriterList();
    }
}
