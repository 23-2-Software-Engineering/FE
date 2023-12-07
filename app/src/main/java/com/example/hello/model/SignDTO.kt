package com.example.hello.model

data class SignInDTO(
    val loginId: String,
    val password: String
)

data class SignInRequestDTO(
    val loginId: String,
    val password: String
)

data class SignInResponseDTO (
    val loginId: String,
    val password: String
)

data class SignUpDTO(
    var loginId: String,
    var password: String,
    var name: String,
    var nickname: String,
    var birth: String,
    var email: String
)

data class SignUpResponseDTO(
    var msg: String,
    var data: SignUpDTO
)