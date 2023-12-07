package com.example.hello

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hello.adapter.CourseAdapter
import com.example.hello.api.CourseCreateService
import com.example.hello.api.CourseUpdateService
import com.example.hello.api.RetrofitClient
import com.example.hello.databinding.CourseBinding
import com.example.hello.model.CourseData
import com.example.hello.model.CourseDto
import com.example.hello.model.CourseInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class Course : AppCompatActivity() {

    private val binding by lazy { CourseBinding.inflate(layoutInflater) }
    var courseDto = CourseDto(null, null, CourseData(null, ArrayList()), null, null)
    var dayNum:Int = 0
    var targetCourse = CourseInfo(0, "", "")
    var listAdapter = CourseAdapter(
        onClickAddList = {
            addListItem(it)
        },
        onClickUpdateList = {
            updateListItem(it)
        }
    )
    val retrofit: Retrofit = RetrofitClient.getInstance()
    val createApi: CourseCreateService = retrofit.create(CourseCreateService::class.java)
    val updateApi: CourseUpdateService = retrofit.create(CourseUpdateService::class.java)
    lateinit var authToken: String

    @SuppressLint("NotifyDataSetChanged")
    val getAddLocResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val date:Calendar = intent.getSerializableExtra("startDate") as Calendar
            date.add(Calendar.DATE, dayNum - 1)
            courseDto.courseData.courseContent.get(dayNum).add(
                CourseInfo(result.data?.getStringExtra("id").toString().toInt(),
                    result.data?.getStringExtra("name").toString(),
                    "${date.get(Calendar.YEAR)}-${date.get(Calendar.MONTH)}-${date.get(Calendar.DAY_OF_MONTH)}"
                )
            )
        }
        listAdapter.notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    val getUpdateLocResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            targetCourse.placeId = result.data?.getStringExtra("id").toString().toInt()
            targetCourse.placeName = result.data?.getStringExtra("name").toString()
        }
        listAdapter.notifyDataSetChanged()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        authToken = intent.getStringExtra("authToken").toString()
        try {
            courseDto = (intent.getSerializableExtra("courseDto") as CourseDto?)!!
            updateCourse()
        } catch (e: NullPointerException){
            newCourse()
        }

        binding.courseList.apply {
            adapter = listAdapter.build(courseDto)
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        binding.back.setOnClickListener{
            finish()
        }
    }

    fun updateCourse(){
        binding.loc.text = courseDto.courseData.courseTitle

        binding.post.setOnClickListener{
            Log.d("test", authToken)
            Log.d("test", courseDto.toString())

            val now = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA).format(Calendar.getInstance().time)
            courseDto.modifiedDate = now

            updateApi.updateCourse("Bearer $authToken", courseDto).enqueue(object
                : Callback<CourseDto> {
                override fun onFailure(call: Call<CourseDto>, t: Throwable) {
                    Log.d("태그", t.message!!)
                }
                override fun onResponse(call: Call<CourseDto>, response: Response<CourseDto>) {
                    if(response.errorBody() == null){
                        Log.d("태그", "response : ${response.body()?.toString()}") // 정상출력

                        finish()
                    }
                    else{
                        Log.d("태그: 에러바디", "response : ${response.errorBody()}")
                        Log.d("태그: 메시지", "response : ${response.message()}")
                        Log.d("태그: 코드", "response : ${response.code()}")
                    }
                }
            })
        }
    }

    fun newCourse(){
        val name = intent.getStringExtra("loc").toString()
        binding.loc.text = name
        val now = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA).format(Calendar.getInstance().time)
        courseDto.courseData.courseTitle = name
        courseDto.createdDate = now
        courseDto.modifiedDate = now

        val startDate:Calendar = intent.getSerializableExtra("startDate") as Calendar
        val endDate:Calendar = intent.getSerializableExtra("endDate") as Calendar

        val days = abs((startDate.timeInMillis - endDate.timeInMillis)/(1000*60*60*24)).toInt()

        for(i in 1..days+1){
            courseDto.courseData.courseContent.add(ArrayList())
        }

        binding.post.setOnClickListener{
            Log.d("test", authToken)
            Log.d("test", courseDto.toString())
            createApi.postCourse("Bearer $authToken", courseDto).enqueue(object
                : Callback<CourseDto> {
                override fun onFailure(call: Call<CourseDto>, t: Throwable) {
                    Log.d("태그", t.message!!)
                }
                override fun onResponse(call: Call<CourseDto>, response: Response<CourseDto>) {
                    if(response.errorBody() == null){
                        Log.d("태그", "response : ${response.body()?.toString()}") // 정상출력
                        val intent = Intent(this@Course, CourseView::class.java)
                        val bundle = Bundle()

                        bundle.putString("authToken", authToken)
                        intent.putExtras(bundle)

                        startActivity(intent)
                        finish()
                    }
                    else{
                        Log.d("태그: 에러바디", "response : ${response.errorBody()}")
                        Log.d("태그: 메시지", "response : ${response.message()}")
                        Log.d("태그: 코드", "response : ${response.code()}")
                    }
                }
            })
        }
    }

    fun addListItem(position: Int) {
        val requestIntent = Intent(this, LocFind::class.java)

        requestIntent.putExtra("place", intent.getStringExtra("loc"))
        requestIntent.putExtra("locId", "")
        requestIntent.putExtra("locName", "")

        getAddLocResult.launch(requestIntent)

        dayNum = position
    }

    fun updateListItem(courseInfo: CourseInfo){
        targetCourse = courseInfo
        val requestIntent = Intent(this, LocFind::class.java)

        requestIntent.putExtra("place", intent.getStringExtra("loc"))
        requestIntent.putExtra("locId", courseInfo.placeId)
        requestIntent.putExtra("locName", courseInfo.placeName)

        getUpdateLocResult.launch(requestIntent)
    }


}