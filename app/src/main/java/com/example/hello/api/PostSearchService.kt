package com.example.hello.api

import com.example.hello.model.PostDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PostSearchService {
    // 게시글 모두 조회
    @GET("/post")
    fun searchPostAll(): Call<ArrayList<PostDTO>>

    // 게시글 아이디로 조회
    @GET("/post/{post_id}")
    fun searchPostById(
        @Path("post_id") post_id: String
    ): Call<ArrayList<PostDTO>>

    // 태그로 게시글 조회
    @GET("/post/by-tag/{name}")
    fun searchPostByTag(
        @Path("name") name: String
    ): Call<ArrayList<PostDTO>>
}