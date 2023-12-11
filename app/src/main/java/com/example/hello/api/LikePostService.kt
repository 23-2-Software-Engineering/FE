package com.example.hello.api

import com.example.hello.model.PostDTO
import retrofit2.Call
import retrofit2.http.*

interface LikePostService {
    //백엔드
    @GET("likes/{postId}")
    fun addLike(
        @Header("Authorization") authorization: String,
        @Path("postId") postId: Int
    ): Call<Boolean>

    @GET("likes/count")
    fun countLikesByPost(
        @Header("Authorization") authorization: String,
        @Query("postId") postId: Int
    ): Call<PostDTO>
}