package com.example.hello.api

import android.app.Application
import com.example.hello.model.CourseDto
import com.example.hello.model.PostDTO

class Utils: Application() {
    private lateinit var loginId: String
    private lateinit var authToken: String
    private lateinit var loc: String
    private var userId: Int? = null
    private var postDTO: PostDTO? = null
    private var courseDTO: CourseDto? = null

    companion object {
        var instance: Utils? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        loginId = ""
        authToken = ""
        loc = ""
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }

    fun init(){
        loginId = ""
        authToken = ""
        loc = ""
    }

    fun setLoginId(id: String){
        this.loginId = id
    }
    fun getLoginId(): String {
        return loginId
    }

    fun setAuthToken(token: String){
        this.authToken = token
    }
    fun getAuthToken(): String {
        return authToken
    }

    fun setLoc(location: String){
        this.loc = location
    }
    fun getLoc(): String {
        return loc
    }

    fun setUserId(id: Int){
        this.userId = id
    }
    fun getUserId(): Int? {
        return userId
    }

    fun setPostDTO(post: PostDTO){
        this.postDTO = post
    }
    fun getPostDTO(): PostDTO? {
        return postDTO
    }
    fun terminatePostDTO(){
        this.postDTO = null
    }

    fun setCourseDTO(course: CourseDto){
        this.courseDTO = course
    }
    fun getCourseDTO(): CourseDto? {
        return courseDTO
    }
    fun terminateCourseDTO(){
        this.courseDTO = null
    }
}