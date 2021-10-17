package com.video.tamas.Retrofit

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.video.tamas.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Dipendra Sharma  on 27-09-2018.
 */
class ApiClient {
    companion object {


        var baseUrl: String = "http://www.tamas.in/api/VideoApp/mobileapi/"

        fun getClient(): Retrofit {

            val mGson: Gson = GsonBuilder()
                    .setLenient()
                    .create()
            val mHttpLoginInterceptor = HttpLoggingInterceptor()
            //here we will set our log level
            mHttpLoginInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val mOkClient: OkHttpClient.Builder = OkHttpClient.Builder().readTimeout(300, TimeUnit.SECONDS).writeTimeout(300, TimeUnit.SECONDS).connectTimeout(300, TimeUnit.SECONDS)
// add logging as last interceptor always
            // we will add login interceptor only in case of debug.

            if (BuildConfig.DEBUG) {
                mOkClient.addInterceptor(mHttpLoginInterceptor)
            }

            val mRetrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(mOkClient.build())
                    .addConverterFactory(GsonConverterFactory.create(mGson))
                    .build()


            return mRetrofit
        }

        //this function only will use for verify imei because the base url will be diffrent.
        fun getClient(instagramUrl: String): Retrofit {

            val mGson: Gson = GsonBuilder()
                    .setLenient()
                    .create()
            val mHttpLoginInterceptor = HttpLoggingInterceptor()
            //here we will set our log level
            mHttpLoginInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val mOkClient: OkHttpClient.Builder = OkHttpClient.Builder().readTimeout(300,
                    TimeUnit.SECONDS).writeTimeout(300, TimeUnit.SECONDS).connectTimeout(300, TimeUnit.SECONDS)
// add logging as last interceptor always
            // we will add login interceptor only in case of debug.

            if (BuildConfig.DEBUG) {
                mOkClient.addInterceptor(mHttpLoginInterceptor)
            }
            val mRetrofit = Retrofit.Builder()
                    .baseUrl(instagramUrl)
                    .client(mOkClient.build())
                    .addConverterFactory(GsonConverterFactory.create(mGson))
                    .build()


            return mRetrofit
        }

    }
}