package com.example.hello.model

data class LikeResponseDTO(
    var userId: String,
    var postId: String,
    var isLiked: Boolean
)
