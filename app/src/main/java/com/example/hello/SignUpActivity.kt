package com.example.hello

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hello.api.RetrofitClient
import com.example.hello.api.SignUpService
import com.example.hello.databinding.ActivitySignUpBinding
import com.example.hello.model.SignUpDTO
import com.example.hello.model.SignUpResponseDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.Thread.sleep

class SignUpActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 회원가입 버튼 눌렀을 때
        binding.signUpRequestBtn.setOnClickListener {
            if (!validateLoginId()) {
                return@setOnClickListener
            }
            if (!validatePwd()) {
                return@setOnClickListener
            }
            if (!validateDuplicatedPwd()) {
                return@setOnClickListener
            }
            if (!validateNickname()) {
                return@setOnClickListener
            }
            if (!validateEmail()) {
                return@setOnClickListener
            }

            requestSingUp()
            Toast.makeText(this, "회원가입 성공!!", Toast.LENGTH_LONG).show()
            finish()
        }

        // 회원가입 취소 버튼
        binding.signUpCancelBtn.setOnClickListener {
            finish()
        }

    }

    // 입력한 아이디가 유효한지 검사
    private fun validateLoginId(): Boolean {
        val loginId: String = binding.tilId.editText?.text.toString()

        if (loginId.isEmpty()) {
            binding.tilId.error = "아이디를 입력해주세요"
            return false
        } else {
            // 입력한 아이디가 이미 존재하는지 검사
            val retrofit: Retrofit = RetrofitClient.getInstance()
            val signUpService = retrofit.create(SignUpService::class.java)
            var isDuplicated: Boolean = false

            val callSync: Call<Boolean> = signUpService.isIdDuplicated(loginId)
            Thread(Runnable() {
                try {
                    isDuplicated = callSync.execute().body() == true
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }).start();

            try {
                sleep(100);
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return if (isDuplicated) {
                binding.tilId.error = "해당 아이디가 이미 존재합니다"
                false
            } else {
                binding.tilId.error = null
                binding.tilId.isErrorEnabled = false
                true
            }
        }
    }

    private fun validateNickname(): Boolean {
        val nickname: String = binding.tilNickname.editText?.text.toString()

        if (nickname.isEmpty()) {
            binding.tilNickname.error = "닉네임을 입력해주세요"
            return false
        } else {
            // 입력한 닉네임이 이미 존재하는지 검사
            val retrofit: Retrofit = RetrofitClient.getInstance()
            val signUpService = retrofit.create(SignUpService::class.java)
            var isDuplicated: Boolean = false

            val callSync: Call<Boolean> = signUpService.isNicknameDuplicated(nickname)
            Thread(Runnable() {
                try {
                    isDuplicated = callSync.execute().body() == true
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }).start();

            try {
                sleep(100);
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return if (isDuplicated) {
                binding.tilNickname.error = "해당 닉네임이 이미 존재합니다"
                false
            } else {
                binding.tilNickname.error = null
                binding.tilNickname.isErrorEnabled = false
                true
            }
        }
    }

    // 이메일 유효성 검사
    private fun validateEmail(): Boolean {
        val value: String = binding.tilEmail.editText?.text.toString()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        return if (value.isEmpty()) {
            binding.tilEmail.error = "이메일을 입력해주세요"
            false
        } else if (!value.matches(emailPattern.toRegex())) {
            binding.tilEmail.error = "이메일 형식이 잘못되었습니다"
            false
        } else {
            binding.tilEmail.error = null
            binding.tilEmail.isErrorEnabled = false
            true
        }
    }

    private fun validatePwd(): Boolean {
        val pwd: String = binding.tilPassword.editText?.text.toString()
        return true
    }

    // 비밀번호 재입력란 유효성 검사
    private fun validateDuplicatedPwd(): Boolean {
        val pwd: String = binding.tilPassword.editText?.text.toString()
        val pwdChk: String = binding.tilPasswordCheck.editText?.text.toString()

        return if (pwd != pwdChk) {
            binding.tilPasswordCheck.error = "비밀번호가 일치하지 않습니다"
            false
        } else {
            binding.tilPasswordCheck.error = null
            binding.tilPasswordCheck.isErrorEnabled = false
            true
        }
    }

    // 서버에 회원가입 요청 보내기
    private fun requestSingUp() {
        var retrofit: Retrofit = RetrofitClient.getInstance()
        var signUpService: SignUpService = retrofit.create(SignUpService::class.java)

        val loginId = binding.tilId.editText?.text.toString()
        val password = binding.tilPassword.editText?.text.toString()
        val email = binding.tilEmail.editText?.text.toString()
        val name = binding.tilName.editText?.text.toString()
        val nickname = binding.tilNickname.editText?.text.toString()
        val birth = binding.tilBirthday.editText?.text.toString()

        val signUpDTO = SignUpDTO(loginId, password, name, nickname, birth, email)

        signUpService.requestSignUp(signUpDTO)
            .enqueue(object : Callback<SignUpResponseDTO> {
                override fun onFailure(call: Call<SignUpResponseDTO>, t: Throwable) {
                    Log.e("FAILURE", t.message.toString())
                    var dialog = AlertDialog.Builder(this@SignUpActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("서버 호출에 실패했습니다.")
                    dialog.show()
                    // signUpBinding.tilId.error = "서버 연결에 실패했습니다"
                }

                override fun onResponse(
                    call: Call<SignUpResponseDTO>,
                    response: Response<SignUpResponseDTO>,
                ) {
                    var signUpResponse = response.body()
                    Log.v("SIGNUP", "MSG: " + signUpResponse?.msg)
                }
            })
    }
}