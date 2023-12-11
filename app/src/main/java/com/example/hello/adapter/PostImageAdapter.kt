package com.example.hello.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hello.databinding.ActivityPostContentsImageBinding
import com.example.hello.databinding.CourseListItemBinding
import com.example.hello.model.CourseInfo
import com.example.hello.model.PostDataDTO

class PostImageAdapter(
    val onClickDelete: (position: Int) -> Unit
)
    :RecyclerView.Adapter<PostImageAdapter.ViewHolder>() {
    lateinit var items:PostDataDTO
    var myPost: Boolean = false

    fun build(i: PostDataDTO, isMyPost: Boolean): PostImageAdapter {
        items = i
        myPost = isMyPost
        return this
    }

    inner class ViewHolder(val binding: ActivityPostContentsImageBinding):
        RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n")
        fun bind(item: String){
            Glide.with(binding.root)
                .load("http://192.168.0.77:8080${item}")
                .into(binding.pictureUrl)
            if(myPost){
                binding.deleteItem3.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder =
        ViewHolder(ActivityPostContentsImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items.pictures[position])
        holder.binding.deleteItem3.setOnClickListener {
            onClickDelete.invoke(position) //1. deleteimage가 눌렸을때 listposition를 전달하면서 onClickDeleteIcon함수를 실행한다.
        }
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

    override fun getItemCount(): Int = items.pictures.size
}