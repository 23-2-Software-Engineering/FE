package com.example.hello.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hello.databinding.MyPostItemBinding
import com.example.hello.model.PostDTO

class PostViewAdapter (
    val onClickView: (courseId: Int) -> Unit
    ) : RecyclerView.Adapter<PostViewAdapter.ViewHolder>() {
        lateinit var items:ArrayList<PostDTO>

        fun build(i: ArrayList<PostDTO>): PostViewAdapter {
            items = i
            return this
        }

        class ViewHolder(val binding: MyPostItemBinding):
            RecyclerView.ViewHolder(binding.root){

            @SuppressLint("SetTextI18n")
            fun bind(item: PostDTO, position: Int){
                binding.postTitle.text = item.title
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
                ViewHolder =
            ViewHolder(MyPostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position], position)
            holder.binding.postId.setOnClickListener{
                onClickView.invoke(items[position].postId!!.toInt())
            }
        }

        interface OnItemClickListener {
            fun onClick(v: View, position: Int)
        }

        fun setItemClickListener(onItemClickListener: OnItemClickListener) {
            this.itemClickListener = onItemClickListener
        }

        private lateinit var itemClickListener : OnItemClickListener

        override fun getItemCount(): Int = items.size
    }