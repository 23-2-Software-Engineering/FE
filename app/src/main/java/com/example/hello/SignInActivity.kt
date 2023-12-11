package com.example.hello


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.hello.api.RetrofitClient
import com.example.hello.api.SignInService
import com.example.hello.api.Utils
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
                    Toast.makeText(this@SignInActivity, "로그인에 실패했습니다.\n비밀번호를 다시 확인해주세요", Toast.LENGTH_LONG).show()
                } else {
                    val utils: Utils = application as Utils
                    utils.init()

                    utils.setAuthToken(response.body()!!.accessToken)
                    utils.setLoginId(response.body()!!.accessToken)

                    val intent = Intent(this@SignInActivity, MainPage::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        })
    }
}