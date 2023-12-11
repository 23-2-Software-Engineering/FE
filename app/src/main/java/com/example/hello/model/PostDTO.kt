package com.example.hello.model

import java.io.Serializable

data class PostDTO(
    var postId: Int?, // create - x, update - o
    var courseId: Int,
    var userId: Int,
    var title: String?,
    var authorName: String?,
    var authorNickname: String?,
    var loginId: String?,
    var email: String?,
    var postData: ArrayList<PostDataDTO>,
    var createdDate: String?,
    var modifiedDate: String?,
    var tags: ArrayList<String>,
    var likes: ArrayList<Int>
): Serializable

data class PostDataDTO(
    var courseTitle: String,
    var places: ArrayList<CourseInfo>,
    var pictures: ArrayList<String>,
    var content: String
): Serializable

