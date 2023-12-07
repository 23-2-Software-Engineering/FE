package com.example.hello

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hello.adapter.CourseViewAdapter
import com.example.hello.api.CourseDeleteService
import com.example.hello.api.CourseListViewService
import com.example.hello.api.CourseViewService
import com.example.hello.api.RetrofitClient
import com.example.hello.databinding.CourseViewBinding
import com.example.hello.model.CourseDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList

class CourseView:AppCompatActivity() {
    private val binding by lazy { CourseViewBinding.inflate(layoutInflater) }
    lateinit var myCourses:ArrayList<CourseDto>
    lateinit var authToken: String
    val listAdapter = CourseViewAdapter(
        onClickDelete = {
            deleteCourse(it)
        },
        onClickView = {
            viewCourse(it)
        },
        onClickPost = {
            createPost(it)
        }
    )
    private val retrofit: Retrofit = RetrofitClient.getInstance()
    private val listViewApi: CourseListViewService = retrofit.create(CourseListViewService::class.java)
    private val deleteApi: CourseDeleteService = retrofit.create(CourseDeleteService::class.java)
    private val viewApi: CourseViewService = retrofit.create(CourseViewService::class.java)

    @SuppressLint("NotifyDataSetChanged")
    val getUpdateLocResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            listAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        authToken = intent.getStringExtra("authToken").toString()

        apiExecute()

        binding.back2.setOnClickListener {
            finish()
        }
    }

    fun apiExecute(){
        listViewApi.viewMyCourse("Bearer $authToken").enqueue(object
            : Callback<ArrayList<CourseDto>> {
            override fun onFailure(call: Call<ArrayList<CourseDto>>, t: Throwable) {
                Log.d("태그", t.message!!)
            }
            override fun onResponse(call: Call<ArrayList<CourseDto>>, response: Response<ArrayList<CourseDto>>) {
                Log.d("태그", "response : ${response.body()?.toString()}") // 정상출력
                myCourses = response.body()!!

                // 전송은 성공 but 서버 4xx 에러
                Log.d("태그: 에러바디", "response : ${response.errorBody()}")
                Log.d("태그: 메시지", "response : ${response.message()}")
                Log.d("태그: 코드", "response : ${response.code()}")

                bind()
            }
        })
    }

    fun bind(){
        listAdapter.build(myCourses)

        binding.myCourseList.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    fun deleteCourse(courseDto: CourseDto) {
        courseDto.courseId?.let {
            deleteApi.deleteCourse("Bearer $authToken", it.toInt()).enqueue(object
                : Callback<CourseDto> {
                override fun onFailure(call: Call<CourseDto>, t: Throwable) {
                    Log.d("태그", t.message!!)
                }

                override fun onResponse(call: Call<CourseDto>, response: Response<CourseDto>) {
                    if(response.errorBody() == null){
                        Log.d("태그", "response : ${response.body()?.toString()}") // 정상출력
                        apiExecute()
                    } else{
                        Log.d("태그: 에러바디", "response : ${response.errorBody()}")
                        Log.d("태그: 메시지", "response : ${response.message()}")
                        Log.d("태그: 코드", "response : ${response.code()}")
                    }
                }
            })
        }
    }

    fun viewCourse(id: Int){
        viewApi.viewCourse("Bearer $authToken", id).enqueue(object
            : Callback<CourseDto> {
            override fun onFailure(call: Call<CourseDto>, t: Throwable) {
                Log.d("태그", t.message!!)
            }

            @SuppressLint("SimpleDateFormat")
            override fun onResponse(call: Call<CourseDto>, response: Response<CourseDto>) {
                if(response.errorBody() == null){
                    Log.d("태그", "response : ${response.body()?.toString()}") // 정상출력
                    val intent = Intent(this@CourseView, Course::class.java)
                    val bundle = Bundle()


                    var date = response.body()!!.courseData.courseContent[0][0].date!!.split("-")

                    var startDate = Calendar.getInstance().apply { set(date[0].toInt(), date[1].toInt(), date[2].toInt()) }


                    bundle.putString("authToken", authToken)
                    bundle.putSerializable("courseDto", response.body())
                    bundle.putString("loc", response.body()!!.courseData.courseTitle)
                    bundle.putSerializable("startDate", startDate)
                    intent.putExtras(bundle)

                    startActivity(intent)
                } else{
                    Log.d("태그: 에러바디", "response : ${response.errorBody()}")
                    Log.d("태그: 메시지", "response : ${response.message()}")
                    Log.d("태그: 코드", "response : ${response.code()}")
                }
            }
        })
    }

    fun createPost(courseDto: CourseDto){
        val intent = Intent(this@CourseView, SelectTag::class.java)
        val bundle = Bundle()

        bundle.putString("authToken", authToken)
        bundle.putSerializable("courseDto", courseDto)
        intent.putExtras(bundle)

        startActivity(intent)
    }
}