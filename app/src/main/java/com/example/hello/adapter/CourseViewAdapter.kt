package com.example.hello.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hello.databinding.CourseViewItemBinding
import com.example.hello.model.CourseDto

class CourseViewAdapter(
    val onClickDelete: (courseDto: CourseDto) -> Unit,
    val onClickView: (courseId: Int) -> Unit,
    val onClickPost: (courseDto: CourseDto) -> Unit
) : RecyclerView.Adapter<CourseViewAdapter.ViewHolder>() {
    lateinit var items:ArrayList<CourseDto>

    fun build(i:ArrayList<CourseDto>): CourseViewAdapter {
        items = i
        return this
    }

    class ViewHolder(val binding: CourseViewItemBinding):
        RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n")
        fun bind(item: CourseDto, position: Int){
            binding.courseTitle.text = "${item.courseData.courseTitle} 여행"
            binding.courseDay.text = "Day ${item.courseData.courseContent.size}"
            binding.courseCreateDay.text = item.modifiedDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder =
        ViewHolder(CourseViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
        holder.binding.deleteItem2.setOnClickListener {
            onClickDelete.invoke(items[position]) //1. deleteimage가 눌렸을때 listposition를 전달하면서 onClickDeleteIcon함수를 실행한다.
        }
        holder.binding.courseLayout.setOnClickListener{
            onClickView.invoke(items[position].courseId!!.toInt())
        }
        holder.binding.postCreate.setOnClickListener {
            onClickPost.invoke(items[position])
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