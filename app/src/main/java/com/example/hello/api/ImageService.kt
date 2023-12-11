package com.example.hello.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImagesUploadService {
    @Multipart
    @POST("uploadImgList")
    fun uploadImg(
        @Part postImgList: List<MultipartBody.Part>
    ): Call<ArrayList<String>>
}

interface ImageUploadService {
    @Multipart
    @POST("uploadImg")
    fun uploadImg(
        @Part postImgList: MultipartBody.Part
    ): Call<String>
}