package com.example.hello.api

import com.example.hello.model.CourseDto
import retrofit2.Call
import retrofit2.http.*

interface CourseCreateService {
    @POST("course/create")
    fun postCourse(
        @Header("Authorization") authorizationHeader: String,
        @Body courseDto: CourseDto
    ): Call<CourseDto>
}

interface CourseListViewService {
    @GET("course/myCourses")
    fun viewMyCourse(
        @Header("Authorization") authorizationHeader: String,
    ): Call<ArrayList<CourseDto>>
}

interface CourseDeleteService{
    @POST("course/delete/{id}")
    fun deleteCourse(
        @Header("Authorization") authorizationHeader: String,
        @Path("id") id: Int
    ): Call<CourseDto>
}

interface CourseViewService{
    @GET("course/view/{id}")
    fun viewCourse(
        @Header("Authorization") authorizationHeader: String,
        @Path("id") id: Int
    ): Call<CourseDto>
}

interface CourseUpdateService{
    @POST("course/update")
    fun updateCourse(
        @Header("Authorization") authorizationHeader: String,
        @Body courseDto: CourseDto
    ): Call<CourseDto>
}