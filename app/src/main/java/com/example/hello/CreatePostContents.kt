package com.example.hello

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hello.adapter.PostCreateAdapter
import com.example.hello.api.*
import com.example.hello.databinding.ActivityPostContentsBinding
import com.example.hello.model.CourseDto
import com.example.hello.model.PostDTO
import com.example.hello.model.PostDataDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreatePostContents : AppCompatActivity() {

    private lateinit var binding: ActivityPostContentsBinding

    private lateinit var postDTO: PostDTO
    private var tags = arrayListOf<String>()
    lateinit var courseData: CourseDto
    var myPost:Boolean = false

    var listAdapter = PostCreateAdapter(
        onClickAddImage = {
            addListItem()
        }
    )

    fun addListItem(){

    }

    val retrofit: Retrofit = RetrofitClient.getInstance()
    val postApi: PostCreateService = retrofit.create(PostCreateService::class.java)
    val updateApi: PostUpdateService = retrofit.create(PostUpdateService::class.java)
    val deleteApi: PostDeleteService = retrofit.create(PostDeleteService::class.java)
    lateinit var authToken: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostContentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authToken = intent.getStringExtra("authToken")!!

        try {
            postDTO = (intent.getSerializableExtra("postDTO") as PostDTO?)!!
            readPost()
        } catch (e: NullPointerException){
            newPost()
        }

        binding.backPageButton.setOnClickListener {
            finish()
        }
        binding.deletePostButton.setOnClickListener{
            deletePost()
        }
        binding.updatePostButton.setOnClickListener{
            updatePost()
        }
        binding.uploadPostButton.setOnClickListener {
            createPost()
        }

        binding.postData.apply {
            adapter = listAdapter.build(postDTO, myPost)
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    @SuppressLint("SetTextI18n")
    fun readPost(){
        tags = postDTO.tags
        binding.title.text = postDTO.title
        binding.tags.text = "#${tags.joinToString(", #")}"

        myPost = postDTO.userId == intent.getStringExtra("userId")!!.toInt()

        if (myPost) {
            binding.updatePostButton.visibility = View.VISIBLE
            binding.deletePostButton.visibility = View.VISIBLE
        }
    }

    fun newPost() {
        val courseData:CourseDto = intent.getSerializableExtra("courseDto") as CourseDto

        tags = intent.getSerializableExtra("tags") as ArrayList<String>

        postDTO = PostDTO(
            null,
            courseData.courseId!!,
            courseData.userId!!,
            null,
            null,
            null,
            null,
            null,
            arrayListOf<PostDataDTO>(),
            null,
            null,
            tags,
            arrayListOf<Int>()
        )

        val days = courseData.courseData.courseContent.size
        for (i in 0 until days){
            postDTO.postData.add(PostDataDTO(
                courseData.courseData.courseTitle!!,
                courseData.courseData.courseContent[i],
                arrayListOf<String>(),
                ""
            ))
        }
        postDTO.title = postDTO.postData[0].courseTitle
        binding.tags.text = "#${tags.joinToString(", #")}"

        binding.uploadPostButton.visibility = View.VISIBLE
        binding.likes.visibility = View.GONE

        myPost = true
    }

    private fun createPost() {
        postApi.postPost("Bearer $authToken", postDTO).enqueue(object
            : Callback<PostDTO> {
            override fun onFailure(call: Call<PostDTO>, t: Throwable) {
                Log.d("태그", t.message!!)
            }

            override fun onResponse(call: Call<PostDTO>, response: Response<PostDTO>) {
                if (response.errorBody() == null) {
                    Log.d("태그", "response : ${response.body()?.toString()}") // 정상출력
                    val intent = Intent(this@CreatePostContents, ReadMyPostList::class.java)
                    val bundle = Bundle()

                    bundle.putString("authToken", authToken)
                    intent.putExtras(bundle)

                    startActivity(intent)
                    finish()
                } else {
                    Log.d("태그: 에러바디", "response : ${response.errorBody()}")
                    Log.d("태그: 메시지", "response : ${response.message()}")
                    Log.d("태그: 코드", "response : ${response.code()}")
                }
            }
        })
    }

    fun updatePost() {
        val now = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA).format(Calendar.getInstance().time)
        postDTO.modifiedDate = now

        val id = postDTO.postId!!

        updateApi.updatePost("Bearer $authToken", id, postDTO).enqueue(object
            : Callback<PostDTO> {
            override fun onFailure(call: Call<PostDTO>, t: Throwable) {
                Log.d("태그", t.message!!)
            }

            override fun onResponse(call: Call<PostDTO>, response: Response<PostDTO>) {
                if (response.errorBody() == null) {
                    Log.d("태그", "response : ${response.body()?.toString()}") // 정상출력
                    val intent = Intent(this@CreatePostContents, CourseView::class.java)
                    val bundle = Bundle()

                    bundle.putString("authToken", authToken)
                    intent.putExtras(bundle)

                    startActivity(intent)
                    finish()
                } else {
                    Log.d("태그: 에러바디", "response : ${response.errorBody()}")
                    Log.d("태그: 메시지", "response : ${response.message()}")
                    Log.d("태그: 코드", "response : ${response.code()}")
                }
            }
        })
    }

    fun deletePost(){

    }
}
