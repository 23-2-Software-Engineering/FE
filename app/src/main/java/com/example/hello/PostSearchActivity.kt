package com.example.se_proj

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hello.adapter.PostSearchAdapter
import com.example.hello.databinding.ActivityPostSearchBinding
import com.example.se_proj.data.model.PostDTO
import com.example.se_proj.data.model.PostSeacrhDTO
import com.example.se_proj.databinding.ActivityPostSearchBinding
import com.example.se_proj.service.CourseSearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class PostSearchActivity : AppCompatActivity() {

    private val postSearchBinding by lazy { ActivityPostSearchBinding.inflate(layoutInflater) }
    private lateinit var postSeacrhDTO: PostSeacrhDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        // 모든 게시글 검색
        searchAllPost()

        super.onCreate(savedInstanceState)
        setContentView(postSearchBinding.root)

        val gridView: GridView = findViewById(R.id.postGridView) as GridView

        // 어댑터 생성 및 설정
        val gridAdapter = PostSearchAdapter(this, postSeacrhDTO.postList)
        gridView.setAdapter(gridAdapter)

        // 뒤로가기 버튼 클릭 리스너
        postSearchBinding.backBtn.setOnClickListener {
            finish()
        }

        // 포스트 검색 버튼 클릭 리스너
        postSearchBinding.searchRequestBtn.setOnClickListener {
            searchPostByTag()
            gridView.adapter = PostSearchAdapter(this, postSeacrhDTO.postList)
        }

        // 그리드뷰에 클릭 리스너 설정 (포스트 이미지 클릭시 해당 포스트뷰로 이동)
        gridView.setOnItemClickListener { parent, view, position, id ->

            // 클릭된 그리드 항목의 데이터를 가져오기
            val clickedPost = postSeacrhDTO.postList[position]

            Toast.makeText(this,"그림 클릭!!", Toast.LENGTH_LONG).show()
            // 클릭된 항목의 postId를 가져와서 다음 액티비티에 전달
//            val intent = Intent(this, ReadPost::class.java)
//            intent.putExtra("EXTRA_MESSAGE", clickedPost.postId)
//            startActivity(intent)

            // 혹은 PostDTO를 직접 전달하는 경우
            // intent.putExtra("POST_DATA", clickedPost)
            // startActivity(intent)

        }

    }

    // 포스트 전체 검색
    private fun searchAllPost(): PostSeacrhDTO {
        var retrofit: Retrofit = RetrofitClient.getInstance()
        var courseSearchService = retrofit.create(CourseSearchService::class.java)

        courseSearchService!!.searchAllPost()
            .enqueue(object : Callback<ArrayList<PostDTO>> {
                override fun onFailure(call: Call<ArrayList<PostDTO>>, t: Throwable) {
                    Log.e("FAILURE", t.message.toString())
                    var dialog = AlertDialog.Builder(this@PostSearchActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("서버 호출에 실패했습니다.")
                    dialog.show()
                    // signUpBinding.tilId.error = "서버 연결에 실패했습니다"
                }

                override fun onResponse(
                    call: Call<ArrayList<PostDTO>>,
                    response: Response<ArrayList<PostDTO>>
                ) {
                    postSeacrhDTO.postList = response.body()!!
                    Log.v("POST SEARCH", "MSG: " + response!!.body()!!.size)
                    Log.v("POST SEARCH", "ERRMSG : " + response.errorBody())
                }

            })
        return postSeacrhDTO
    }

    // 태그로 게시글 검색
    private fun searchPostByTag(): PostSeacrhDTO {
        val searchTag: String = postSearchBinding.tietSearch.text.toString()
        var retrofit: Retrofit = RetrofitClient.getInstance()
        var courseSearchService = retrofit.create(CourseSearchService::class.java)

        courseSearchService!!.searchPostByTag(searchTag)
            .enqueue(object : Callback<PostSeacrhDTO> {
                override fun onFailure(call: Call<PostSeacrhDTO>, t: Throwable) {
                    Log.e("FAILURE", t.message.toString())
                    var dialog = AlertDialog.Builder(this@PostSearchActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("서버 호출에 실패했습니다.")
                    dialog.show()
                    // signUpBinding.tilId.error = "서버 연결에 실패했습니다"
                }

                override fun onResponse(
                    call: Call<PostSeacrhDTO>,
                    response: Response<PostSeacrhDTO>
                ) {
                    postSeacrhDTO = response.body()!!
                    Log.v("POST SEARCH BY TAG", "ERRMSG : " + response.errorBody())
                }
            })
        return postSeacrhDTO
    }

    // 아이디로 게시글 검색 -> 이 페이지에서는 안넣어도 될듯?
}