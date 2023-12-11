package com.example.hello

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.example.hello.databinding.ActivitySelectTagBinding

class SelectTag : AppCompatActivity() {

    private lateinit var binding: ActivitySelectTagBinding
    private var tags = arrayListOf<String>()
    var loginId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectTagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginId = intent.getStringExtra("loginId").toString()

        binding.backPageButton.setOnClickListener{
            finish()
        }
        binding.nextPageButton.setOnClickListener {
            moveToCreatePostActivity()
        }

        binding.toggleButtonActivity.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedTags("Activity", isChecked)
        }
        binding.toggleButtonNature.setOnCheckedChangeListener {_, isChecked ->
            updateSelectedTags("Nature", isChecked)
        }
        binding.toggleButtonArt.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedTags("Art", isChecked)
        }
        binding.toggleButtonSnsHotPlace.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedTags("SnsHotPlace", isChecked)
        }
        binding.toggleButtonCulture.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedTags("Culture", isChecked)
        }
        binding.toggleButtonFamily.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedTags("Family", isChecked)
        }
        binding.toggleButtonFriend.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedTags("Friend", isChecked)
        }
        binding.toggleButtonLover.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedTags("Lover", isChecked)
        }
        binding.toggleButtonFamousPlace.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedTags("FamousPlace", isChecked)
        }
        binding.toggleButtonRestaurant.setOnCheckedChangeListener { _, isChecked ->
            updateSelectedTags("Restaurant", isChecked)
        }
    }

    private fun updateSelectedTags(tag: String, isChecked: Boolean) {
        if (isChecked) {
            tags.add(tag)
        } else {
            tags.remove(tag)
        }
    }

    private fun moveToCreatePostActivity() {
        val authToken = intent.getStringExtra("authToken")
        val courseDto = intent.getSerializableExtra("courseDto")

        // 선택된 태그를 CreatePostContents 액티비티로 전달
        val intent = Intent(this, CreatePostContents::class.java)
        val bundle = Bundle()

        bundle.putSerializable("tags", tags)
        bundle.putSerializable("courseDto", courseDto)
        bundle.putString("authToken", authToken)
        bundle.putString("loginId", loginId)
        intent.putExtras(bundle)

        startActivity(intent)
    }
}
