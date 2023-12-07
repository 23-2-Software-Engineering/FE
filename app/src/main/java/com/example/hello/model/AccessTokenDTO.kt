package com.example.hello.model

data class AccessTokenDTO(
    var grantType: String,
    var accessToken: String,
    var loginId: String
)
