package com.example.hello.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hello.databinding.ActivityPostContentsItemBinding
import com.example.hello.model.PostDTO
import com.example.hello.model.PostDataDTO
import kotlinx.android.synthetic.main.activity_post_contents_image.view.*

class PostCreateAdapter(
    val onClickAddImage: (position: Int) -> Unit
)
    :RecyclerView.Adapter<PostCreateAdapter.ViewHolder>() {
    private lateinit var items: PostDTO
    var myPost: Boolean = false
    var contents: ArrayList<String> = arrayListOf()

    override fun getItemCount(): Int = items.postData.size
    fun contentsUpload(): ArrayList<String> = contents

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items.postData[position], position)
        holder.binding.addPictureButton.setOnClickListener {
            onClickAddImage.invoke(position)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            PostCreateAdapter.ViewHolder =
        ViewHolder(ActivityPostContentsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    inner class ViewHolder(val binding: ActivityPostContentsItemBinding):
        RecyclerView.ViewHolder(binding.root){
        var listAdapter = PostImageAdapter(
            onClickDelete = {
                deleteData(it)
            }
        )

        lateinit var postData: PostDataDTO
        @SuppressLint("NotifyDataSetChanged")
        fun deleteData(position: Int){
            postData.pictures.removeAt(position)
            listAdapter.notifyDataSetChanged()
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: PostDataDTO, position: Int){
            if(myPost){
                binding.addPictureButton.visibility = View.VISIBLE
                binding.content.visibility = View.VISIBLE
                binding.content2.visibility = View.GONE
                binding.content.setText(item.content)

                binding.content.addTextChangedListener(object: TextWatcher{
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun afterTextChanged(p0: Editable?) {
                        contents[position] = binding.content.text.toString()
                    }
                })
            }

            binding.content2.text = item.content
            postData = item
            binding.courseTitle.text = "day ${position + 1} : ${item.places[0].date}"
            var courseList = ""
            for(i in 0 until item.places.size){
                courseList += "${item.places[i].placeName}\n"
            }
            binding.placeName.text = courseList
            binding.imageList.apply {
                adapter = listAdapter.build(item, myPost)
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }
        }
    }

    fun build(i: PostDTO, isMyPost: Boolean): PostCreateAdapter {
        items = i
        myPost = isMyPost
        for(index in 0 until i.postData.size){
            contents.add(i.postData[index].content)
        }
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