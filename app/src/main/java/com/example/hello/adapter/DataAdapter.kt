package com.example.hello.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hello.databinding.CourseListItemBinding
import com.example.hello.model.CourseInfo

class DataAdapter(
    val onClickDelete: (courseDate: CourseInfo) -> Unit,
    val onClickUpdate: (position: Int) -> Unit
)
    :RecyclerView.Adapter<DataAdapter.ViewHolder>() {
    lateinit var items:ArrayList<CourseInfo>

    fun build(i:ArrayList<CourseInfo>): DataAdapter {
        items = i
        return this
    }

    class ViewHolder(val binding: CourseListItemBinding):
        RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n")
        fun bind(item: CourseInfo, position: Int){
            binding.courseListNum.text = (position + 1).toString()
            binding.locName.text = "  " + item.placeName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder =
        ViewHolder(CourseListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
        holder.binding.deleteItem.setOnClickListener {
            onClickDelete.invoke(items[position]) //1. deleteimage가 눌렸을때 listposition를 전달하면서 onClickDeleteIcon함수를 실행한다.
        }
        holder.binding.locName.setOnClickListener{
            onClickUpdate.invoke(position)
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