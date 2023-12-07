package com.example.hello.api

import com.example.hello.model.CourseDto
import com.example.hello.model.PostDTO
import retrofit2.Call
import retrofit2.http.*

interface PostCreateService {
    @POST("post")
    fun postPost(
        @Header("Authorization") authorizationHeader: String,
        @Body postDto: PostDTO
    ): Call<PostDTO>
}

interface PostDeleteService{
    @DELETE("post/{id}")
    fun deletePost(
        @Header("Authorization") authorizationHeader: String,
        @Path("id") id: Int
    )
}

interface PostViewService{
    @GET("post/{id}")
    fun viewPost(
        @Path("id") id: Int
    ): Call<PostDTO>
}

interface PostUpdateService{
    @PUT("post/{id}")
    fun updatePost(
        @Header("Authorization") authorizationHeader: String,
        @Path("id") id: Int,
        @Body postDto: PostDTO
    ): Call<PostDTO>
}

interface PostListViewService {
    @GET("post/myPosts")
    fun viewMyCourse(
        @Header("Authorization") authorizationHeader: String,
    ): Call<ArrayList<PostDTO>>
}