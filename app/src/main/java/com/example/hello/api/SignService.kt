package com.example.hello.api

import com.example.hello.model.AccessTokenDTO
import com.example.hello.model.SignInRequestDTO
import com.example.hello.model.SignUpDTO
import com.example.hello.model.SignUpResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SignInService {
    @POST("/login")
    fun requestLogin(
        @Body userInfo: SignInRequestDTO
    ): Call<AccessTokenDTO>

}

interface SignUpService {
    // 회원가입 리퀘스트
    @POST("/signup")
    fun requestSignUp(
        @Body data: SignUpDTO
    ): Call<SignUpResponseDTO>

    //아이디 중복 확인
    @GET("/login-id/{loginId}/duplicate")
    fun isIdDuplicated(
        @Path("loginId") loginId: String
    ): Call<Boolean>

    // 닉네임 중복 확인
    @GET("/login-nickname/{nickname}/duplicate")
    fun isNicknameDuplicated(
        @Path("nickname") nickname: String
    ): Call<Boolean>
}