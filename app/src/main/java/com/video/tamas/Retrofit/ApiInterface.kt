package com.video.tamas.Retrofit

import com.video.tamas.Models.CommonResponseModel
import com.video.tamas.Utils.Common
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {



    /*@Multipart
    @POST("service.php?servicename=videoupload")
fun uploadVideo(@Part description:MultipartBody.Part,
                    @Part user_id:MultipartBody.Part,
                    @Part categories_id:MultipartBody.Part,
                    @Part sub_category:MultipartBody.Part,
                    @Part song_id:MultipartBody.Part,
                    @Part draft:MultipartBody.Part,
                    @Part comment_status:MultipartBody.Part,
                    @Part video: MultipartBody.Part):Call<String>
*/
    @Multipart
    @POST("service_video.php?servicename=videoupload")
    fun uploadVideo(@Part("description") description:RequestBody,
                    @Part("user_id") user_id:RequestBody,
                    @Part("categories_id") categories_id:RequestBody,
                    @Part("sub_category") sub_category:RequestBody,
                    @Part("song_id") song_id:RequestBody,
                    @Part("draft") draft:RequestBody,
                    @Part("comment_status") comment_status:RequestBody,
                    @Part video: MultipartBody.Part):Call<String>


    @POST("service_video.php?servicename=forget_password")
    fun forgotPassword()

}