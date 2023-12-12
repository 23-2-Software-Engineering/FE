package com.example.hello.model

data class LikeResponseDTO(
    var userId: Int,
    var postId: String,
    var isLiked: Boolean
)
