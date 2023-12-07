package com.example.hello.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hello.databinding.ActivityPostContentsItemBinding
import com.example.hello.model.PostDTO
import com.example.hello.model.PostDataDTO
import java.util.*

class PostCreateAdapter(
    val onClickAddImage: () -> Unit
)
    :RecyclerView.Adapter<PostCreateAdapter.ViewHolder>() {
    private lateinit var items: PostDTO
    var myPost: Boolean = false

    override fun getItemCount(): Int = items.postData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items.postData[position], position)
        holder.binding.addPictureButton.setOnClickListener {
            onClickAddImage.invoke()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            PostCreateAdapter.ViewHolder =
        ViewHolder(ActivityPostContentsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    inner class ViewHolder(val binding: ActivityPostContentsItemBinding):
        RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n")
        fun bind(item: PostDataDTO, position: Int){
            if(myPost){
                binding.content.isFocusable = true
                binding.content.isClickable = true
                binding.addPictureButton.visibility = View.VISIBLE
            }

            binding.courseTitle.text = "day ${position + 1} : ${item.places[0].date}"
            var courseList = ""
            for(i in 0 until item.places.size){
                courseList += "${item.places[i].placeName}\n"
            }
            binding.placeName.text = courseList
        }
    }

    fun build(i: PostDTO, isMyPost: Boolean): PostCreateAdapter {
        items = i
        myPost = isMyPost
        return this
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
}