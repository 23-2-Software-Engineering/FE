package com.example.hello.model

import java.io.Serializable

data class CourseDto(
    var courseId: Int?,
    var userId: Int?,
    var courseData: CourseData,
    var createdDate: String?,
    var modifiedDate: String?
): Serializable

data class CourseData(
    var courseTitle: String?,
    var courseContent: ArrayList<ArrayList<CourseInfo>>
): Serializable

data class CourseInfo(
    var placeId: Int? = null,
    var placeName: String? = null,
    var date: String? = null
): Serializable