package com.example.hello

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hello.R
import com.example.hello.adapter.PostSearchAdapter
import com.example.hello.api.PostSearchService
import com.example.hello.api.RetrofitClient
import com.example.hello.databinding.ActivityPostSearchBinding
import com.example.hello.model.CourseInfo
import com.example.hello.model.PostDTO
import com.example.hello.model.PostDataDTO
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_post_search.tietSearch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.NullPointerException

class PostSearchActivity : AppCompatActivity() {

    private val postSearchBinding by lazy { ActivityPostSearchBinding.inflate(layoutInflater) }
    private lateinit var gridView: GridView
    private lateinit var adapter: PostSearchAdapter
    private var postList = ArrayList<PostDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(postSearchBinding.root)

        gridView = findViewById(R.id.postGridView)

//        val dummyPostList = ArrayList<PostSearchTestDTO>()
//        for(i: Int in 1..8) {
//            dummyPostList.add(PostSearchTestDTO("Post Title${i}"))
//        }
//        gridView.adapter = PostSearchTestAdapter(this, dummyPostList)

        // 모든 게시글 검색
        try {
            searchAllPost()
        } catch (e :NullPointerException) {
            Log.e("SEARCH POST", "ERR: 포스트 찾기에서 NULL POINTER EXCEPTION 발생")
        }
        gridView.adapter = PostSearchAdapter(this, postList)

        // 포스트 이미지 클릭
        gridView.setOnItemClickListener{parent, view, position, id ->

        }

        // 뒤로 가기 버튼 클릭
        postSearchBinding.backBtn.setOnClickListener {
            finish()
        }

        // 검색 버튼 클릭
        postSearchBinding.searchRequestBtn.setOnClickListener {
            val tietSearch: TextInputEditText = findViewById(R.id.tietSearch)
            searchPostByTag(tietSearch.text.toString())
            gridView.adapter = PostSearchAdapter(this, postList)
        }


    }

    // 포스트 전체 검색
    private fun searchAllPost() {
        var retrofit: Retrofit = RetrofitClient.getInstance()
        var courseSearchService = retrofit.create(PostSearchService::class.java)

        val callSync: Call<ArrayList<PostDTO>> = courseSearchService.searchPostAll()
        Thread(Runnable() {
            try {
                postList = callSync.execute().body()!!
            } catch (e: IOException) {
                Log.e("SEACRCH POST ALL", "ERR: " + callSync.execute().errorBody())
                e.printStackTrace()
            }
        }).start();

        try {
            Thread.sleep(1000);
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        Log.d("SEARCH POST ALL", "MSG: " + postList.toString())
    }

    // 태그로 게시글 검색
    private fun searchPostByTag(tag: String) {
        val searchTag: String = postSearchBinding.tietSearch.text.toString()
        var retrofit: Retrofit = RetrofitClient.getInstance()
        var courseSearchService = retrofit.create(PostSearchService::class.java)

        val callSync: Call<ArrayList<PostDTO>> = courseSearchService.searchPostByTag(tag)
        Thread(Runnable() {
            try {
                postList = callSync.execute().body()!!
            } catch (e: IOException) {
                Log.e("SEACRCH POST TAG", "ERR: " + callSync.execute().errorBody())
                e.printStackTrace()
            }
        }).start();

        try {
            Thread.sleep(1000);
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        Log.d("SEARCH POST TAG", "MSG: " + postList.toString())
    }
}