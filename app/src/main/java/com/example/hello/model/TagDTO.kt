package com.example.hello.model

import java.io.Serializable

data class TagDTO(
    val activity: String,
    val art: String,
    val snsHotPlace: String,
    val culture: String,
    val family: String,
    val friend: String,
    val lover: String,
    val famousPlace: String,
    val nature: String,
    val restaurant: String

) :Serializable {

    fun getAllTags(): List<String> {
        return listOf(activity, art, snsHotPlace, culture, family, friend, lover, famousPlace, nature, restaurant)
    }
}

