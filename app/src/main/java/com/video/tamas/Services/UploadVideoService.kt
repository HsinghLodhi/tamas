package com.video.tamas.Services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.video.tamas.Activities.HomeActivity
import com.video.tamas.R
import com.video.tamas.Retrofit.ApiClient
import com.video.tamas.Retrofit.ApiInterface
import com.video.tamas.Utils.ProgressRequestBody
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UploadVideoService : Service() {
    var mIntent: Intent? = null
    override fun onCreate() {
        //startForeground(1,createNotification(applicationContext))
        if (mIntent != null) {
            //uploadVideo()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
//mIntent=p0
        //uploadVideo()
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification(applicationContext))
        mIntent = intent
        uploadVideo()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {

        super.onDestroy()
    }

    private fun createNotification(mContext: Context): Notification {
        var channelId = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = "my_service"
            val channelName = "My Background Service"
            val chan = NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_NONE)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)


        } else {
            channelId = ""

        }

        val builder = NotificationCompat.Builder(mContext, channelId)

        builder.setOngoing(true)
        builder.setAutoCancel(true)

        val notifyIntent = Intent(mContext, HomeActivity::class.java)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(mContext, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(mContext.getString(R.string.app_name))
                .setContentText("Swift Driver Getting your Location....")
                .setContentIntent(pendingIntent)

        return builder.build()


    }

    private fun uploadVideo() {
        val from = mIntent!!.getStringExtra("from")
        var categoryStr = ""
        var subCategoryStr = ""
        if (from == "category") {
            categoryStr = "2"
            subCategoryStr = mIntent!!.getStringExtra("sub_category")
        } else {
            categoryStr = "1"
            subCategoryStr = "1"
        }

        val description: RequestBody = RequestBody.create(MediaType.parse("text"), mIntent!!.getStringExtra("description"))
        //val user_id: MultipartBody.Part= MultipartBody.Part.createFormData("user_id", mIntent!!.getStringExtra("user_id"))
        val user_id: RequestBody = RequestBody.create(MediaType.parse("text"), mIntent!!.getStringExtra("user_id"))
        val category: RequestBody = RequestBody.create(MediaType.parse("text"), categoryStr)
        val subCategory: RequestBody = RequestBody.create(MediaType.parse("text"), subCategoryStr)
        val song_id: RequestBody = RequestBody.create(MediaType.parse("text"), mIntent!!.getStringExtra("song_id"))
        val draft: RequestBody = RequestBody.create(MediaType.parse("text"), mIntent!!.getStringExtra("draft"))
        val comment_status: RequestBody = RequestBody.create(MediaType.parse("text"), mIntent!!.getStringExtra("comment_status"))
        //val comment_status: MultipartBody.Part= MultipartBody.Part.createFormData("comment_status", mIntent!!.getStringExtra("comment_status"))
        val sourceFile = File(mIntent!!.getStringExtra("filePath"))
        //val videoFile:RequestBody=RequestBody.create(MediaType.parse("video/*"), sourceFile)
        val fileBody = ProgressRequestBody(sourceFile, HomeActivity.mVideoUploadProgressHandler)
        val videoMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData("file_name", sourceFile?.name, fileBody)

        val mInterface = ApiClient.getClient().create(ApiInterface::class.java)
        val mCall = mInterface.uploadVideo(description, user_id, category, subCategory, song_id, draft, comment_status, videoMultiPart)
        mCall.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.v("dip", "inside fail : " + t.message)
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.v("dip", "is success. : " + response)
                stopForeground(true)
                stopService(Intent(applicationContext, UploadVideoService::class.java))
            }


        })


    }


    /*class UploadVideoAsynTask(var mContext:Context): AsyncTask<Void, Integer, String>() {
    override fun onPreExecute() {
        // setting progress bar to zero
        val settingsDialog =        Dialog()
        settingsDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.dialog_progress, null))
        txtPercentage = settingsDialog.findViewById<TextView>(R.id.txtPercentage)
        progressBar = settingsDialog.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.setProgress(0)   // Main Progress
        progressBar.setSecondaryProgress(100) // Secondary Progress
        progressBar.setMax(100) // Maximum Progress
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_circle))
        settingsDialog.show()
        //settingsDialog.setCancelable(false);
        super.onPreExecute()
    }

    protected override fun onProgressUpdate(vararg progress: Int) {
        // Making progress bar visible
        progressBar.setVisibility(View.VISIBLE)

        // updating progress bar value
        progressBar.setProgress(progress[0])
        progressBar.setIndeterminate(true)

        // updating percentage value
        txtPercentage.setText(progress[0].toString() + "%")
    }

    override fun doInBackground(vararg params: Void): String {
        return uploadFile()
    }

    private fun uploadFile(): String {
        var responseString: String? = null

        val httpclient = DefaultHttpClient()
        val httppost = HttpPost("http://www.tamas.in/api/VideoApp/mobileapi/service_video.php?servicename=videoupload")

        try {
            val entity = AndroidMultiPartEntity(
                    AndroidMultiPartEntity.ProgressListener { num -> publishProgress((num / totalSize.toFloat() * 100).toInt()) })

            val sourceFile = File(filePath)

            // Adding file data to http body
            entity.addPart("file_name", FileBody(sourceFile))
            entity.addPart("description", StringBody(toServerUnicodeEncoded))
            entity.addPart("user_id", StringBody(userId))
            if (from.equals("category", ignoreCase = true)) {

                entity.addPart("categories_id", StringBody("2"))
                entity.addPart("sub_category", StringBody(subCategory))

            } else {
                entity.addPart("categories_id", StringBody("1"))
                entity.addPart("sub_category", StringBody("1"))
            }
            entity.addPart("song_id", StringBody(songId))
            entity.addPart("draft", StringBody(draftStatus))
            // Extra parameters if you want to pass to server
            entity.addPart("comment_status",
                    StringBody(commentStatus))
            totalSize = entity.contentLength
            httppost.entity = entity
            Log.wtf("uploadResponse", entity.toString())
            // Making server call
            val response = httpclient.execute(httppost)
            val r_entity = response.entity
            Log.wtf("uploadResponse", r_entity.toString())
            val statusCode = response.statusLine.statusCode
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity)
            } else {
                responseString = "Error occurred! Http Status Code: $statusCode"
            }

        } catch (e: ClientProtocolException) {
            responseString = e.toString()
        } catch (e: IOException) {
            responseString = e.toString()
        }

        return responseString

    }

    override fun onPostExecute(result: String) {
        Log.e(TAG, "Response from server: $result")
        // showing the server response in an alert dialog
        showAlert(result)
        progressBar.setVisibility(View.GONE)
        super.onPostExecute(result)
    }


}*/

}