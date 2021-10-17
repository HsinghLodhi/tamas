package com.video.tamas.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.video.tamas.R;
import com.video.tamas.Utils.AndroidMultiPartEntity;
import com.video.tamas.Utils.Common;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class EditProfileActivity extends AppCompatActivity {
    EditText etName, etUserName, etMobileNo, etEmail, etBio;
    CircleImageView ivProfilePic;
    Button btnEditProfile;
    String name, username, mobileno, email, bio;
    NetworkUtils networkUtils;
    DeviceResourceManager resourceManager;
    private String userId, selectedImagePath;
    private static final int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    Boolean isImageChanged = false;
    ImageView ivBack;
    private LazyLoader lazyLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        networkUtils = new NetworkUtils(this);
        resourceManager = new DeviceResourceManager(this);
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "");
        lazyLoader=findViewById(R.id.lazyLoaderEditProfile);
        etName = findViewById(R.id.etName);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        ivBack = findViewById(R.id.ivBack);
        etUserName = findViewById(R.id.etUsername);
        etMobileNo = findViewById(R.id.etMobileNo);
        etEmail = findViewById(R.id.etEmailAddress);
        etBio = findViewById(R.id.etBio);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        Glide.with(this).load(R.drawable.default_user).into(ivProfilePic);
        getUserInfo();
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadFileToServer().execute();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Photo options");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCapture(data);
            }
        }
    }

    private void onCapture(Intent data) {
        Bitmap thumbnail = null;
        File file = null;
        try {
            thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

            Uri tempUri = getImageUri(getApplicationContext(), thumbnail);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            //  File finalFile = new File(getRealPathFromURI(tempUri));
            selectedImagePath = getRealPathFromURI(tempUri);
            Log.wtf("cameraPath", selectedImagePath);
            isImageChanged = true;
            Glide.with(EditProfileActivity.this)
                    .load(selectedImagePath)
                    .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.mipmap.ic_person))
                    .into(ivProfilePic);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onSelectFromGalleryResult(Intent data) {
        try {
            Uri selectedImageUri = data.getData();
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = getContentResolver().query(selectedImageUri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            selectedImagePath = cursor.getString(column_index);
            Log.wtf("path", selectedImagePath);
            isImageChanged = true;
            Glide.with(EditProfileActivity.this)
                    .load(selectedImagePath)
                    .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.mipmap.ic_person))
                    .into(ivProfilePic);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void getUserInfo() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.getUserInfo, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                Log.wtf("response", result);
                lazyLoader.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.optString("status");
                    if (status.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String userName = jsonObject1.optString("username");
                            String name = jsonObject1.optString("first_name");
                            String email = jsonObject1.optString("email");
                            String mobile = jsonObject1.optString("mobile");
                            String bio = jsonObject1.optString("user_bio");
                            String profilePic = jsonObject1.optString("photoURL");
                            etUserName.setText(userName);
                            etName.setText(name);
                            etEmail.setText(email);
                            etMobileNo.setText(mobile);
                            etBio.setText(bio);
                            Glide.with(EditProfileActivity.this)
                                    .load(profilePic)
                                    .apply(placeholderOf(R.drawable.progress_animation).transform(new CircleCrop()).error(R.mipmap.ic_person))
                                    .into(ivProfilePic);
                        }


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

    public void editUserInfo() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .content("Editing profile ...")
                .cancelable(false)
                .progress(true, 0)
                .show();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("username", etUserName.getText().toString());
            jsonBody.put("email", etEmail.getText().toString());
            jsonBody.put("mobile", etMobileNo.getText().toString());
            jsonBody.put("file_name", selectedImagePath);
            jsonBody.put("first_name", etName.getText().toString());
            jsonBody.put("last_name", "");
            jsonBody.put("address", "");
            jsonBody.put("zip", "");
            jsonBody.put("country", "");
            jsonBody.put("region", "");
            jsonBody.put("city", "");
            jsonBody.put("devicetype", "");
            jsonBody.put("devicetoken", "");
            jsonBody.put("user_bio", etBio.getText().toString());
            jsonBody.put("height", "");
            jsonBody.put("weight", "");
            Log.wtf("json", jsonBody.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkUtils.postDataVolley(Config.MethodName.editProfile, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                materialDialog.dismiss();
                Log.wtf("result", result);


            }

            @Override
            public void onFailureResponse(String result) {
                materialDialog.dismiss();
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

        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.MethodName.editProfile);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {

                            }
                        });
                if (isImageChanged) {
                    File sourceFile = new File(selectedImagePath);
                    entity.addPart("file_name", new FileBody(sourceFile));
                } else {
                    entity.addPart("file_name", new StringBody(""));
                }
                entity.addPart("user_id", new StringBody(userId));
                entity.addPart("username", new StringBody(etUserName.getText().toString()));
                entity.addPart("email", new StringBody(etEmail.getText().toString()));
                entity.addPart("mobile", new StringBody(etMobileNo.getText().toString()));
                entity.addPart("first_name", new StringBody(etName.getText().toString()));
                entity.addPart("last_name", new StringBody(""));
                entity.addPart("address", new StringBody(""));
                entity.addPart("zip", new StringBody(""));
                entity.addPart("country", new StringBody(""));
                entity.addPart("region", new StringBody(""));
                entity.addPart("city", new StringBody(""));
                entity.addPart("devicetype", new StringBody("android"));
                entity.addPart("devicetoken", new StringBody(Common.getDeviceId()));
                entity.addPart("user_bio", new StringBody(etBio.getText().toString()));
                entity.addPart("height", new StringBody(""));
                entity.addPart("weight", new StringBody(""));

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
            Log.e("response", "Response from server: " + result);
            // showing the server response in an alert dialog
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                String message = jsonObject.optString("message");
                String status = jsonObject.optString("status");
                if (status.equalsIgnoreCase("success")) {
                    Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            ///showAlert(result);
            isImageChanged = false;
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

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
