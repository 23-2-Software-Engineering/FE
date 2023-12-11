package com.example.hello

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.GridView
import com.example.hello.adapter.PostSearchAdapter
import com.example.hello.api.PostSearchService
import com.example.hello.api.RetrofitClient
import com.example.hello.databinding.ActivityPostSearchBinding
import com.example.hello.model.PostDTO
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Retrofit
import java.io.IOException
import java.lang.NullPointerException

class PostSearchActivity : AppCompatActivity() {

    private val postSearchBinding by lazy { ActivityPostSearchBinding.inflate(layoutInflater) }
    private lateinit var gridView: GridView
    private var postList = ArrayList<PostDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(postSearchBinding.root)

        gridView = findViewById(R.id.postGridView)

        try {
            // 모든 게시글 검색 리퀘스트
            // searchAllPost()

            // 게시글 추천 리퀘스트
            recommendPost()
        } catch (e: NullPointerException) {
            Log.e("RECOMMEND POST", "ERR: 포스트 추천에서 NULL POINTER EXCEPTION 발생")
        }
        gridView.adapter = PostSearchAdapter(this, postList)

        // 뒤로 가기 버튼 클릭
        postSearchBinding.backBtn.setOnClickListener {
            finish()
        }

        // 검색 버튼 클릭
        postSearchBinding.searchRequestBtn.setOnClickListener {
            val tietSearch: TextInputEditText = findViewById(R.id.tietSearch)
            if (!tietSearch.text!!.isEmpty()) {
                searchPostByTag(tietSearch.text.toString())
            } else {
                recommendPost()
            }

            gridView.adapter = PostSearchAdapter(this, postList)
        }

    }

    // 포스트 전체 검색
    private fun searchAllPost() {
        var retrofit: Retrofit = RetrofitClient.getInstance()
        var postSearchService = retrofit.create(PostSearchService::class.java)

        val callSync: Call<ArrayList<PostDTO>> = postSearchService.searchPostAll()
        Thread(Runnable() {
            try {
                postList = callSync.execute().body()!!
            } catch (e: IOException) {
                Log.e("SEACRCH POST ALL", "ERR: " + callSync.execute().errorBody())
                e.printStackTrace()
            }
        }).start()

        try {
            Thread.sleep(1000);
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        Log.d("SEARCH POST ALL", "MSG: " + postList.toString())
    }

    // 게시글 좋아요 순으로 추천
    private fun recommendPost() {
        var retrofit: Retrofit = RetrofitClient.getInstance()
        var postSearchService = retrofit.create(PostSearchService::class.java)

        Log.d("REQUEST RECOMMEND POST", "")
        val callSync: Call<ArrayList<PostDTO>> = postSearchService.searchPostOrderByLikes()
        Thread(Runnable() {
            try {
                postList = callSync.execute().body()!!
            } catch (e: IOException) {
                Log.e("RECOMMEND POST", "ERR: " + callSync.execute().errorBody())
                e.printStackTrace()
            }
        }).start();

        try {
            Thread.sleep(1000);
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        Log.d("RECOMMEND POST", "MSG: " + postList.toString())
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