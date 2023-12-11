package com.example.hello

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hello.adapter.PostViewAdapter
import com.example.hello.api.*
import com.example.hello.databinding.ActivityReadMyPostListBinding
import com.example.hello.model.PostDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.collections.ArrayList
import kotlin.math.log

class ReadMyPostList : AppCompatActivity() {
    private val binding by lazy { ActivityReadMyPostListBinding.inflate(layoutInflater) }
    lateinit var myPosts:ArrayList<PostDTO>
    lateinit var authToken: String
    lateinit var loginId: String
    lateinit var utils: Utils

    val listAdapter = PostViewAdapter(
        onClickView = {
            viewCourse(it)
        }
    )
    private val retrofit: Retrofit = RetrofitClient.getInstance()
    private val listViewApi: PostListViewService = retrofit.create(PostListViewService::class.java)
    private val viewApi: PostViewService = retrofit.create(PostViewService::class.java)

    @SuppressLint("NotifyDataSetChanged")
    val getUpdateLocResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            listAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        utils = application as Utils

        authToken = utils.getAuthToken()
        loginId = utils.getLoginId()

        apiExecute()

        binding.backPageButton.setOnClickListener {
            finish()
        }
    }

    fun apiExecute(){
        listViewApi.viewMyCourse("Bearer $authToken").enqueue(object
            : Callback<ArrayList<PostDTO>> {
            override fun onFailure(call: Call<ArrayList<PostDTO>>, t: Throwable) {
                Log.d("태그", t.message!!)
            }
            override fun onResponse(call: Call<ArrayList<PostDTO>>, response: Response<ArrayList<PostDTO>>) {
                Log.d("태그", "response : ${response.body()?.toString()}") // 정상출력
                myPosts = response.body()!!

                // 전송은 성공 but 서버 4xx 에러
                Log.d("태그: 에러바디", "response : ${response.errorBody()}")
                Log.d("태그: 메시지", "response : ${response.message()}")
                Log.d("태그: 코드", "response : ${response.code()}")

                bind()
            }
        })
    }

    fun bind(){
        listAdapter.build(myPosts)

        binding.viewMyPostList.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    fun viewCourse(id: Int){
        viewApi.viewPost(id).enqueue(object
            : Callback<PostDTO> {
            override fun onFailure(call: Call<PostDTO>, t: Throwable) {
                Log.d("태그", t.message!!)
            }

            @SuppressLint("SimpleDateFormat")
            override fun onResponse(call: Call<PostDTO>, response: Response<PostDTO>) {
                if(response.errorBody() == null){
                    Log.d("태그", "response : ${response.body()?.toString()}") // 정상출력
                    val intent = Intent(this@ReadMyPostList, CreatePostContents::class.java)

//                    var date = response.body()!!.courseData.courseContent[0][0].date!!.split("-")
//                    var startDate = Calendar.getInstance().apply { set(date[0].toInt(), date[1].toInt(), date[2].toInt()) }

                    utils.setPostDTO(response.body()!!)

//                    bundle.putSerializable("startDate", startDate)
                    startActivity(intent)
                } else{
                    Log.d("태그: 에러바디", "response : ${response.errorBody()}")
                    Log.d("태그: 메시지", "response : ${response.message()}")
                    Log.d("태그: 코드", "response : ${response.code()}")
                }
            }
        })
    }
}
