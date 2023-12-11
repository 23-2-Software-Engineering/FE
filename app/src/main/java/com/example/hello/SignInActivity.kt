package com.example.hello


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.hello.api.RetrofitClient
import com.example.hello.api.SignInService
import com.example.hello.databinding.ActivitySignInBinding
import com.example.hello.model.AccessTokenDTO
import com.example.hello.model.SignInRequestDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class SignInActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySignInBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.signInBtn.setOnClickListener {
            val loginId = binding.textInputSignInId.text.toString()
            val password = binding.textInputPassword.text.toString()

            Log.v("LOGIN", "loginId : " + loginId)
            Log.v("LOGIN", "password : " + password)

            val userInfo: SignInRequestDTO = SignInRequestDTO(loginId, password)
            requestSignIn(userInfo)
        }

        binding.goSignUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    fun requestSignIn(userInfo: SignInRequestDTO) {
        val retrofit: Retrofit = RetrofitClient.getInstance()
        val signInService = retrofit.create(SignInService::class.java)

        // 서버에 로그인 리퀘스트
        signInService.requestLogin(userInfo).enqueue(object : Callback<AccessTokenDTO> {
            override fun onFailure(call: Call<AccessTokenDTO>, t: Throwable) {
                Log.e("FAILURE", t.message.toString())
                val dialog = AlertDialog.Builder(this@SignInActivity)
                dialog.setTitle("에러")
                dialog.setMessage("호출에 실패했습니다.")
                dialog.show()
            }

            override fun onResponse(
                call: Call<AccessTokenDTO>,
                response: Response<AccessTokenDTO>,
            ) {
                if (response.errorBody() != null) {
                    Log.d("로그인 실패", response.errorBody().toString())
                } else {
                    Log.v("LOGIN", "GrantType : ${response.body()!!.grantType}")
                    Log.v("LOGIN", "AccessToken : ${response.body()!!.accessToken}")
                    Log.v("LOGIN", "LoginId : ${response.body()!!.loginId}")

//                        Utils.setGrantType(response.body()!!.grantType)
//                        Utils.setAccessToken(response.body()!!.accessToken)

                    val intent = Intent(this@SignInActivity, MainPage::class.java)
                    intent.putExtra("authToken", response.body()!!.accessToken)
                    intent.putExtra("loginId", response.body()!!.loginId)
                    startActivity(intent)
                    finish()
                }
            }
        })
    }
}