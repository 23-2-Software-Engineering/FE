package com.example.hello.api

import com.example.hello.model.LikeResponseDTO
import com.example.hello.model.PostDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface LikePostService {
    @GET("likes/{postId}")
    fun addLike(
        @Header("Authorization") authorization: String,
        @Path("postId") postId: Int
    ): Call<LikeResponseDTO>

    @GET("likes/count")
    fun countLikesByPost(
        @Header("Authorization") authorization: String,
        @Query("postId") postId: Int
    ): Call<PostDTO>
}
