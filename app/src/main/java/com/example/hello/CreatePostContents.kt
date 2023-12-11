package com.example.hello

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hello.adapter.PostCreateAdapter
import com.example.hello.api.*
import com.example.hello.databinding.ActivityPostContentsBinding
import com.example.hello.model.CourseDto
import com.example.hello.model.LikeResponseDTO
import com.example.hello.model.PostDTO
import com.example.hello.model.PostDataDTO
import kotlinx.android.synthetic.main.activity_post_contents.authorNicknameView
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CreatePostContents : AppCompatActivity() {

    private lateinit var binding: ActivityPostContentsBinding

    private lateinit var postDTO: PostDTO
    private var tags = arrayListOf<String>()
    lateinit var courseData: CourseDto
    var loginId: String = ""
    var myPost:Boolean = false
    val retrofit: Retrofit = RetrofitClient.getInstance()
    val postApi: PostCreateService = retrofit.create(PostCreateService::class.java)
    val updateApi: PostUpdateService = retrofit.create(PostUpdateService::class.java)
    val deleteApi: PostDeleteService = retrofit.create(PostDeleteService::class.java)
    val imgApi: ImagesUploadService = retrofit.create(ImagesUploadService::class.java)
    val likePostService: LikePostService = retrofit.create(LikePostService::class.java)

    lateinit var authToken: String
    lateinit var utils: Utils

    var listAdapter = PostCreateAdapter(
        onClickAddImage = {
            addListItem(it)
        }
    )

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    var position: Int = 0
    @SuppressLint("NotifyDataSetChanged")
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        val imageList: ArrayList<MultipartBody.Part> = ArrayList()

        //결과 코드 OK , 결가값 null 아니면
        if(it.resultCode == RESULT_OK){

            //멀티 선택은 clipData
            if(it.data?.clipData != null){ //멀티 이미지

                //선택한 이미지 갯수
                val count = it.data!!.clipData!!.itemCount

                for(index in 0 until count){
                    //이미지 담기
                    val imageUri = it.data!!.clipData!!.getItemAt(index).uri
                    val file = File(absolutelyPath(imageUri, this))
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("postImgList", file.name, requestFile)
                    //이미지 추가
                    imageList.add(body)
                }

            }else{ //싱글 이미지
                it.data?.data?.let { uri ->
                    val imagePath = it.data!!.data
                    val file = File(absolutelyPath(imagePath, this))
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("postImgList", file.name, requestFile)
                    //이미지 추가
                    imageList.add(body)
                }
            }
            sendImage(imageList)
            Log.d("사진", imageList.toString())

        }
    }

    fun absolutelyPath(path: Uri?, context : Context): String {
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        var result = c?.getString(index!!)

        return result!!
    }

    fun sendImage(body: ArrayList<MultipartBody.Part>){
        imgApi.uploadImg(body).enqueue(object: Callback<ArrayList<String>>{
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ArrayList<String>>, response: Response<ArrayList<String>>) {
                if(response.isSuccessful){
                    for(i in 0 until (response.body()?.size ?: 0)){
                        postDTO.postData[position].pictures.add(response.body()!![i])
                    }
                    Log.d("결과", postDTO.toString())
                    listAdapter.notifyDataSetChanged()
                }else{
                    Toast.makeText(this@CreatePostContents, "이미지 전송 실패", Toast.LENGTH_SHORT).show()
                    Log.d("응답", response.toString())
                }
            }

            override fun onFailure(call: Call<ArrayList<String>>, t: Throwable) {
                Log.d("testt", t.message.toString())
            }
        })
    }

    fun addListItem(pos: Int){
        position = pos
        verifyStoragePermissions(this)
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        //멀티 선택 기능
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        activityResult.launch(intent)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostContentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        utils = application as Utils
        authToken = utils.getAuthToken()
        loginId = utils.getLoginId()

        try {
            postDTO = utils.getPostDTO()!!
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
        binding.likesButton.setOnClickListener {
            likePost()
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
        binding.authorNicknameView.text = postDTO.authorNickname
        binding.title.text = postDTO.title
        binding.tags.text = "#${tags.joinToString(", #")}"

        val id = utils.getLoginId()
        Log.d("내 아이디", id)
        Log.d("포스트  아이디", postDTO.loginId!!)
        myPost = postDTO.loginId == id

        if (myPost) {
            binding.updatePostButton.visibility = View.VISIBLE
            binding.deletePostButton.visibility = View.VISIBLE
        }

        try {
            if(postDTO.likes.contains(utils.getUserId())){
                binding.likesButton.setImageResource(R.drawable.likes_on_button_image)
            }
        } catch (e: Exception){
            binding.likesButton.setImageResource(R.drawable.likes_off_button_image)
        }
    }

    @SuppressLint("SetTextI18n")
    fun newPost() {
        val courseData:CourseDto = utils.getCourseDTO()!!

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
        binding.likesButton.visibility = View.GONE

        myPost = true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun createPost() {
        val contents = listAdapter.contentsUpload()
        for(i in 0 until contents.size){
            postDTO.postData[i].content = contents[i]
        }

        postApi.postPost("Bearer $authToken", postDTO).enqueue(object
            : Callback<PostDTO> {
            override fun onFailure(call: Call<PostDTO>, t: Throwable) {
                Log.d("태그", t.message!!)
            }

            override fun onResponse(call: Call<PostDTO>, response: Response<PostDTO>) {
                if (response.errorBody() == null) {
                    Log.d("태그", "response : ${response.body()?.toString()}") // 정상출력
                    val intent = Intent(this@CreatePostContents, ReadMyPostList::class.java)

                    utils.terminateCourseDTO()
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

        val contents = listAdapter.contentsUpload()
        for(i in 0 until contents.size){
            postDTO.postData[i].content = contents[i]
        }

        updateApi.updatePost("Bearer $authToken", id, postDTO).enqueue(object
            : Callback<PostDTO> {
            override fun onFailure(call: Call<PostDTO>, t: Throwable) {
                Log.d("태그", t.message!!)
            }

            override fun onResponse(call: Call<PostDTO>, response: Response<PostDTO>) {
                if (response.errorBody() == null) {
                    Log.d("태그", "response : ${response.body()?.toString()}") // 정상출력
                    utils.terminatePostDTO()
                    finish()
                } else {
                    Log.d("태그: 에러바디", "response : ${response.errorBody()}")
                    Log.d("태그: 메시지", "response : ${response.message()}")
                    Log.d("태그: 코드", "response : ${response.code()}")
                }
            }
        })
    }

    fun deletePost() {
        postDTO.postId?.let {
            deleteApi.deletePost("Bearer $authToken", it.toInt()).enqueue(object
                : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("태그", t.message!!)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    utils.terminatePostDTO()
                    finish()
                }
            })
        }
    }

    //백엔드 LikeService.java 에서 addLike : Boolean 처리되어 있음
    fun likePost() {
        val postId = postDTO.postId!!
        likePostService.addLike("Bearer $authToken", postId).enqueue(object
            : Callback<LikeResponseDTO> {
            override fun onFailure(call: Call<LikeResponseDTO>, t: Throwable) {
                Log.d("태그", t.message!!)
            }

            override fun onResponse(call: Call<LikeResponseDTO>, response: Response<LikeResponseDTO>) {
                if (response.errorBody() == null) {
                    if(response.body()!!.isLiked){
                        binding.likesButton.setImageResource(R.drawable.likes_on_button_image)
                    } else{
                        binding.likesButton.setImageResource(R.drawable.likes_off_button_image)
                    }
                    utils.setUserId(response.body()!!.userId)
                } else {
                    Log.d("태그: 에러바디", "response : ${response.errorBody()}")
                    Log.d("태그: 메시지", "response : ${response.message()}")
                    Log.d("태그: 코드", "response : ${response.code()}")
                }
            }
        })
    }
}
