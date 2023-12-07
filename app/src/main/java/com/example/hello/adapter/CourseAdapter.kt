package com.example.hello.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hello.databinding.CourseListBinding
import com.example.hello.model.CourseDto
import com.example.hello.model.CourseInfo
import java.util.*
import kotlin.collections.ArrayList

class CourseAdapter(
    val onClickAddList: (position: Int) -> Unit,
    val onClickUpdateList: (courseInfo: CourseInfo) -> Unit
)
    :RecyclerView.Adapter<CourseAdapter.ViewHolder>() {
    private lateinit var items: CourseDto

    override fun getItemCount(): Int = items.courseData.courseContent.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items.courseData.courseContent[position], position)
        holder.binding.addList.setOnClickListener {
            onClickAddList.invoke(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder = ViewHolder(
                CourseListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                parent.context
            )

    fun build(i: CourseDto): CourseAdapter {
        items = i
        return this
    }

    inner class ViewHolder(val binding: CourseListBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        private var course = arrayListOf<CourseInfo>()
        var listAdapter = DataAdapter(
            onClickDelete = {
                deleteData(it)
            },
            onClickUpdate = {
                updateData(it)
            }
        )

        @SuppressLint("SetTextI18n")
        fun bind(item: ArrayList<CourseInfo>, position: Int) {
            val calendar = Calendar.getInstance()
            with(binding)
            {
                course = item
                dayNum.text = "Day " + (position + 1)
                calendar.add(Calendar.DATE, position)
                dayDetail.text = "${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.DAY_OF_MONTH)}" +
                        " " +
                        calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.KOREAN)
                addNum.text = (course.size + 1).toString()
                courseListData.apply {
                    adapter = listAdapter.build(course)
                    layoutManager = LinearLayoutManager(context)
                }
            }
        }

        @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
        fun deleteData(courseInfo: CourseInfo) {
            course.remove(courseInfo)
            binding.addNum.text = (course.size + 1).toString()
            listAdapter.notifyDataSetChanged()
        }

        fun updateData(position: Int){
            onClickUpdateList.invoke(course.get(position))
        }
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
}